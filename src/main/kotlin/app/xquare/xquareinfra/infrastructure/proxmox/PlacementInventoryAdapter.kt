package app.xquare.xquareinfra.infrastructure.proxmox

import app.xquare.xquareinfra.application.placement.Candidate
import app.xquare.xquareinfra.application.placement.Inventory
import app.xquare.xquareinfra.application.placement.NodeKey
import app.xquare.xquareinfra.application.placement.PlacementInventory
import app.xquare.xquareinfra.application.placement.PlacementPolicy
import app.xquare.xquareinfra.application.placement.PlacementProfile
import app.xquare.xquareinfra.application.placement.PoolKey
import app.xquare.xquareinfra.application.placement.Template
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.springframework.stereotype.Component
import java.io.IOException
import java.time.Instant
import java.util.concurrent.TimeUnit

@Component
class PlacementInventoryAdapter(
    client: OkHttpClient,
    private val config: ProxmoxProperties,
    private val mapper: ObjectMapper,
) : PlacementInventory {
    private val http by lazy {
        ProxmoxFeignConfig()
            .buildHttpClient(client, config)
            .newBuilder()
            .apply {
                interceptors().clear() // Do not log catalog identifiers or cache an inventory snapshot.
                cache(null)
                followRedirects(false)
                followSslRedirects(false)
                retryOnConnectionFailure(false)
                callTimeout(3, TimeUnit.SECONDS)
            }.build()
    }

    override fun read(profile: PlacementProfile): Inventory? {
        if (!profile.valid() || !clusterIdPattern.matches(config.clusterId)) return null
        val start = Instant.now()
        return try {
            val api = Observation(start)
            val nodes = api.rows("nodes")
            val online =
                profile.nodes.filterValues { name ->
                    nodes.singleOrNull { it.text("node") == name }?.text("status") == "online"
                }
            val stores = online.values.associateWith { api.rows("nodes/$it/storage") }
            val template = profile.template
            val minFinalDiskBytes =
                if (template is Template.Qemu) {
                    val source = stores[template.sourceNode] ?: return null
                    if (template.sourceStores.any { source.store(it, "images") == null }) return null
                    val actual = api.data("nodes/${template.sourceNode}/qemu/${template.vmid}/config", "current" to "1")
                    actual.sourceDiskBytes(template) ?: return null
                } else {
                    0L
                }
            val candidates =
                online.mapNotNull { (alias, name) ->
                    val local = stores.getValue(name)
                    val target =
                        local.store(profile.targetStore, if (template is Template.Qemu) "images" else "rootdir") ?: return@mapNotNull null
                    when (template) {
                        is Template.Qemu ->
                            if (name != template.sourceNode &&
                                template.sourceStores.any {
                                    stores.getValue(template.sourceNode).store(it, "images")?.flag("shared") != true ||
                                        local.store(it, "images")?.flag("shared") != true
                                }
                            ) {
                                return@mapNotNull null
                            }
                        is Template.Lxc -> {
                            val origin = stores[template.sourceNode]?.store(template.store, "vztmpl") ?: return@mapNotNull null
                            val archiveStore = local.store(template.store, "vztmpl") ?: return@mapNotNull null
                            if (name != template.sourceNode &&
                                (origin.flag("shared") != true || archiveStore.flag("shared") != true)
                            ) {
                                return@mapNotNull null
                            }
                            val volumes = api.rows("nodes/$name/storage/${template.store}/content", "content" to "vztmpl")
                            if (volumes.singleOrNull { it.text("volid") == template.volume } == null) return@mapNotNull null
                        }
                    }
                    val shared = target.flag("shared") ?: return@mapNotNull null
                    val raw = nodes.single { it.text("node") == name }
                    val cores = raw.number("maxcpu")?.takeIf { it in 1..Int.MAX_VALUE.toLong() } ?: return@mapNotNull null
                    Candidate(
                        alias,
                        cores.toInt(),
                        raw.path("cpu").takeIf { it.isNumber }?.asDouble() ?: return@mapNotNull null,
                        raw.number("maxmem") ?: return@mapNotNull null,
                        raw.number("mem") ?: return@mapNotNull null,
                        target.number("avail") ?: return@mapNotNull null,
                        PoolKey(config.clusterId, profile.targetStore, name.takeUnless { shared }),
                        NodeKey(config.clusterId, name),
                        minFinalDiskBytes,
                    )
                }
            Inventory(start, candidates)
        } catch (_: IOException) {
            null
        } catch (_: IllegalArgumentException) {
            null
        }
    }

    private inner class Observation(
        val started: Instant,
    ) {
        fun rows(
            path: String,
            query: Pair<String, String>? = null,
        ): List<JsonNode> = data(path, query).also { require(it.isArray) }.toList()

        fun data(
            path: String,
            query: Pair<String, String>? = null,
        ): JsonNode {
            require(Instant.now() < started.plus(PlacementPolicy.maxAge))
            val url = config.url.toHttpUrl()
            require(url.isHttps || url.host == "127.0.0.1" || url.host == "::1")
            require(config.tokenId.isNotBlank() && config.tokenSecret.isNotBlank())
            val endpoint =
                url
                    .newBuilder()
                    .addPathSegments(
                        "api2/json/$path",
                    ).apply { query?.let { addQueryParameter(it.first, it.second) } }
                    .build()
            val request =
                Request
                    .Builder()
                    .url(endpoint)
                    .header("Authorization", "PVEAPIToken=${config.tokenId}=${config.tokenSecret}")
                    .header("Cache-Control", "no-cache, no-store")
                    .build()
            return http.newCall(request).execute().use { response ->
                if (!response.isSuccessful || response.header("Age")?.let { it != "0" } == true) throw IOException()
                val bytes = response.body?.byteStream()?.readNBytes(MAX_BYTES + 1) ?: throw IOException()
                require(bytes.size <= MAX_BYTES)
                mapper.readTree(bytes)?.get("data")?.takeUnless { it.isNull } ?: throw IOException()
            }
        }
    }

    private companion object {
        const val MAX_BYTES = 1024 * 1024
        val clusterIdPattern = Regex("[A-Za-z0-9][A-Za-z0-9_.-]{0,63}")
        val uuidPattern = Regex("[0-9A-Fa-f]{8}(?:-[0-9A-Fa-f]{4}){3}-[0-9A-Fa-f]{12}")
        val digestPattern = Regex("[0-9a-fA-F]{40}")
        val labelPattern = Regex("[A-Za-z0-9](?:[A-Za-z0-9-]{0,61}[A-Za-z0-9])?")
        val guestOsTypes = setOf("other", "wxp", "w2k", "w2k3", "w2k8", "wvista", "win7", "win8", "win10", "win11", "l24", "l26", "solaris")
        val simpleDisk = Regex("(?:file=)?([A-Za-z0-9_.-]+):base-([1-9][0-9]*)-disk-[0-9]+,size=([1-9][0-9]*)([KMGT])")

        fun JsonNode.text(key: String) = get(key)?.takeIf { it.isTextual }?.textValue()

        fun JsonNode.number(key: String) = get(key)?.takeIf { it.isIntegralNumber && it.canConvertToLong() }?.longValue()

        fun JsonNode.flag(key: String): Boolean? =
            get(key)?.let {
                when {
                    it.isBoolean -> it.booleanValue()
                    number(key) == 1L -> true
                    number(key) == 0L -> false
                    else -> null
                }
            }

        fun List<JsonNode>.store(
            name: String,
            kind: String,
        ) = singleOrNull { it.text("storage") == name }?.takeIf {
            it.flag("active") == true && it.flag("enabled") == true && kind in it.text("content").orEmpty().split(',')
        }

        private fun JsonNode.supportedField(key: String): Boolean =
            when (key) {
                "scsi0", "template" -> true // Checked separately, including the disk identity and size.
                "vmgenid" -> text(key)?.matches(uuidPattern) == true
                "smbios1" -> text(key)?.let { it.startsWith("uuid=") && uuidPattern.matches(it.substring(5)) } == true
                "boot" -> text(key) == "order=scsi0"
                "digest" -> text(key)?.matches(digestPattern) == true
                "name" -> text(key)?.matches(labelPattern) == true
                "description" -> text(key)?.length?.let { it <= 8192 } == true
                "cores", "sockets" -> number(key)?.let { it in 1..Int.MAX_VALUE.toLong() } == true
                "memory" -> text(key)?.let { it.matches(Regex("[1-9][0-9]{0,9}")) && (it.toIntOrNull() ?: 0) >= 16 } == true
                "ostype" -> text(key) in guestOsTypes
                else -> false
            }

        fun JsonNode.sourceDiskBytes(template: Template.Qemu): Long? {
            // Only the explicitly supported single-volume full-clone shape is eligible, even locally.
            if (!isObject || flag("template") != true || fieldNames().asSequence().any { !supportedField(it) }) return null
            val disk = simpleDisk.matchEntire(text("scsi0") ?: return null) ?: return null
            val (store, vmid, quantity, unit) = disk.destructured
            if (setOf(store) != template.sourceStores || vmid != template.vmid.toString()) return null
            val count = quantity.toLongOrNull() ?: return null
            val multiplier = 1L shl (10 * ("KMGT".indexOf(unit.single()) + 1))
            return if (count <= Long.MAX_VALUE / multiplier) count * multiplier else null
        }
    }
}
