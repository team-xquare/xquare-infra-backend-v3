package app.xquare.xquareinfra.infrastructure.proxmox

import app.xquare.xquareinfra.adapters.inbound.local.placement.AuthorizedPlacementScope
import app.xquare.xquareinfra.adapters.inbound.local.placement.LocalGuestPlacementApi
import app.xquare.xquareinfra.adapters.inbound.local.placement.PlacementQuery
import app.xquare.xquareinfra.application.placement.Compute
import app.xquare.xquareinfra.application.placement.Demand
import app.xquare.xquareinfra.application.placement.GuestPlacement
import app.xquare.xquareinfra.application.placement.NodeKey
import app.xquare.xquareinfra.application.placement.PlacementDecision
import app.xquare.xquareinfra.application.placement.PlacementProfile
import app.xquare.xquareinfra.application.placement.Reservations
import app.xquare.xquareinfra.application.placement.Template
import com.fasterxml.jackson.databind.ObjectMapper
import com.sun.net.httpserver.HttpServer
import okhttp3.OkHttpClient
import org.junit.jupiter.api.Test
import java.net.InetSocketAddress
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PlacementInventoryContractTest {
    private val qemu = Template.Qemu("n1", 900, setOf("base"))
    private val lxc = Template.Lxc("n1", "base", "base:vztmpl/example.tar.zst")
    private val unavailable = PlacementDecision.Unavailable

    @Test
    fun `QEMU requires the approved backing set and shared source for a remote target`() {
        Fixture().use { f ->
            assertEquals(PlacementDecision.Selected("east"), f.preview(qemu))
            f.shared = 0
            assertEquals(PlacementDecision.Selected("west"), f.preview(qemu))
            f.templateDisk = "other:base-900-disk-0"
            assertEquals(unavailable, f.preview(qemu))
            f.templateDisk = "base:base-900-disk-0"
            f.templateFlag = 0
            assertEquals(unavailable, f.preview(qemu))
            assertTrue("/api2/json/nodes/n1/qemu/900/config?current=1" in f.requests)
            assertFalse(f.requests.any { "offline" in it || "/content" in it })
        }
    }

    @Test
    fun `LXC needs the exact archive on that node and rootdir output storage`() {
        Fixture().use { f ->
            assertEquals(PlacementDecision.Selected("west"), f.preview(lxc))
            assertTrue("/api2/json/nodes/n2/storage/base/content?content=vztmpl" in f.requests)
            f.targetContent = "images"
            assertEquals(unavailable, f.preview(lxc))
            f.targetContent = "rootdir"
            f.active = 0
            assertEquals(unavailable, f.preview(lxc))
            assertFalse(f.requests.any { "/qemu" in it })
        }
    }

    @Test
    fun `malformed metrics are not defaulted to zero and failures expose no upstream details`() {
        Fixture().use { f ->
            f.cpuField = "null"
            assertEquals(unavailable, f.preview(qemu))
            f.fail = true
            assertEquals(unavailable, f.preview(lxc))
            assertFalse(f.badTransport)
        }
    }

    @Test
    fun `unknown alias foreign team absent scope and over quota never query inventory`() {
        Fixture().use { f ->
            assertEquals(unavailable, f.preview(lxc, team = 2))
            assertEquals(unavailable, f.preview(lxc, alias = "other-team-template"))
            assertEquals(unavailable, f.preview(lxc, demand = Demand(1, 16, SOURCE_BYTES + 2)))
            assertEquals(unavailable, f.api.preview(PlacementQuery(1, "standard", Demand(1, 16, 8)), null))
            assertTrue(f.requests.isEmpty())
        }
    }

    @Test
    fun `full clone needs the entire final size and must not shrink its source`() {
        Fixture().use { f ->
            f.freeBytes = 4096
            assertEquals(unavailable, f.preview(qemu, demand = Demand(1, 16, 8)))
            f.freeBytes = SOURCE_BYTES
            assertEquals(unavailable, f.preview(qemu, demand = Demand(1, 16, SOURCE_BYTES - 1)))
            assertEquals(PlacementDecision.Selected("east"), f.preview(qemu))
            f.freeBytes--
            assertEquals(unavailable, f.preview(qemu))
            f.freeBytes = SOURCE_BYTES + 1
            assertEquals(PlacementDecision.Selected("east"), f.preview(qemu, demand = Demand(1, 16, SOURCE_BYTES + 1)))
        }
    }

    @Test
    fun `ambiguous missing or overflowing disk sizes are not selectable`() {
        Fixture().use { f ->
            for (suffix in listOf("", ",size=", ",size=16G,size=1G", ",size=9223372036854775807G", ",size=0G")) {
                f.sizeOption = suffix
                assertEquals(unavailable, f.preview(qemu), suffix)
            }
            f.sizeOption = ",size=16G"
            f.extraConfig = ""","scsi1":"base:base-900-disk-1,size=1G""""
            assertEquals(unavailable, f.preview(qemu))
        }
    }

    @Test
    fun `local devices and unknown options cannot move with a shared disk`() {
        Fixture().use { f ->
            for (field in listOf("serial0" to "/dev/ttyS0", "parallel0" to "/dev/parport0", "unknown-device" to "custom")) {
                f.extraConfig = ""","${field.first}":"${field.second}""""
                assertEquals(unavailable, f.preview(qemu), field.first)
            }
        }
    }

    @Test
    fun `generated UUID alone does not disqualify the same single disk`() {
        Fixture().use { f ->
            f.shared = 0
            assertEquals(PlacementDecision.Selected("west"), f.preview(qemu))
            f.extraConfig = ""","vmgenid":"9a322604-fd26-4da7-a4fd-72c5980ad44b""""
            assertEquals(PlacementDecision.Selected("west"), f.preview(qemu))
            f.shared = 1
            assertEquals(PlacementDecision.Selected("east"), f.preview(qemu))
        }
    }

    @Test
    fun `imported single disk boots only from the inspected scsi0`() {
        Fixture().use { f ->
            val oneGiB = 1024L * 1024 * 1024
            f.memoryCapacity = 4 * oneGiB
            val demand = Demand(1, oneGiB, SOURCE_BYTES)
            val id = "9a322604-fd26-4da7-a4fd-72c5980ad44b"
            f.extraConfig =
                ""","name":"fixture-vm","cores":1,"memory":"1024","ostype":"l26","vmgenid":"$id"""" +
                ""","smbios1":"uuid=28f771ac-e71b-4976-a637-f92706034386","boot":"order=scsi0""""
            assertEquals(PlacementDecision.Selected("east"), f.preview(qemu, demand = demand))
            f.shared = 0
            assertEquals(PlacementDecision.Selected("west"), f.preview(qemu, demand = demand))
        }
    }

    @Test
    fun `invalid metadata and unverified boot devices still reject the template even locally`() {
        Fixture().use { f ->
            f.shared = 0
            val values =
                listOf(
                    "vmgenid" to "1",
                    "vmgenid" to "not-a-uuid",
                    "vmgenid" to "/dev/ttyS0",
                    "smbios1" to "uuid=9a322604-fd26-4da7-a4fd-72c5980ad44b,serial=extra",
                    "smbios1" to "uuid=bad",
                    "boot" to "order=scsi1",
                    "boot" to "order=scsi0;net0",
                    "boot" to "legacy=c",
                    "serial0" to "/dev/ttyS0",
                    "future-option" to "unknown",
                )
            for ((key, value) in values) {
                f.extraConfig = ""","$key":"$value""""
                assertEquals(unavailable, f.preview(qemu), "$key=$value")
            }
            for (jsonValue in listOf("null", "1", "true", "{}")) {
                f.extraConfig = ""","vmgenid":$jsonValue"""
                assertEquals(unavailable, f.preview(qemu), jsonValue)
            }
        }
    }

    @Test
    fun `compute holds are the same after catalog aliases change`() {
        Fixture().use { f ->
            val physical = "n1"
            f.aliases = mapOf("west" to physical)
            assertEquals(PlacementDecision.Selected("west"), f.preview(qemu))
            for (held in listOf(Compute(4, 0), Compute(0, 924))) {
                f.holds = Reservations(mapOf(NodeKey("fixture-cluster", physical) to held), emptyMap())
                assertEquals(unavailable, f.preview(qemu))
                f.aliases = mapOf("east" to physical)
                assertEquals(unavailable, f.preview(qemu))
                f.aliases = mapOf("west" to physical)
            }
            f.holds = Reservations(emptyMap(), emptyMap())
            f.aliases = mapOf("east" to physical)
            assertEquals(PlacementDecision.Selected("east"), f.preview(qemu))
        }
    }

    @Test
    fun `a matching archive name on another local store is not identity`() {
        Fixture().use { f ->
            f.secondArchive = "example.tar.zst"
            f.shared = 1
            assertEquals(PlacementDecision.Selected("east"), f.preview(lxc))
            f.shared = 0
            assertEquals(PlacementDecision.Selected("west"), f.preview(lxc))
        }
    }

    private companion object {
        const val SOURCE_BYTES = 16L * 1024 * 1024 * 1024
    }

    private class Fixture : AutoCloseable {
        var shared = 1
        var memoryCapacity = 1024L
        var freeBytes = SOURCE_BYTES
        var sizeOption = ",size=16G"
        var extraConfig = ""
        var secondArchive = "unrelated.tar.zst"
        var aliases = mapOf("west" to "n1", "east" to "n2", "cold" to "offline")
        var holds = Reservations(emptyMap(), emptyMap())
        var templateFlag = 1
        var active = 1
        var templateDisk = "base:base-900-disk-0"
        var cpuField: String? = null
        var targetContent = "images,rootdir"
        var fail = false

        @Volatile var badTransport = false
        val requests = CopyOnWriteArrayList<String>()
        val server: HttpServer = HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0)
        val api: LocalGuestPlacementApi

        init {
            server.createContext("/") { exchange ->
                val uri = exchange.requestURI.toString()
                requests += uri
                if (exchange.requestMethod != "GET" ||
                    exchange.requestHeaders.getFirst("Authorization") != "PVEAPIToken=fixture=sample"
                ) {
                    badTransport = true
                }
                val route = uri.removePrefix("/api2/json/")
                val data =
                    when (route) {
                        "nodes" -> """[${metric("n1", "0.25")},${metric("n2", "0.0")},{"node":"offline","status":"offline"}]"""
                        "nodes/n1/storage", "nodes/n2/storage" -> storageRows()
                        "nodes/n1/qemu/900/config?current=1" ->
                            """{"template":$templateFlag,"scsi0":"$templateDisk$sizeOption"$extraConfig}"""
                        "nodes/n1/storage/base/content?content=vztmpl" -> """[{"volid":"base:vztmpl/example.tar.zst"}]"""
                        "nodes/n2/storage/base/content?content=vztmpl" -> """[{"volid":"base:vztmpl/$secondArchive"}]"""
                        else -> null
                    }
                val body = if (fail || data == null) "synthetic-private-detail" else """{"data":$data}"""
                val bytes = body.toByteArray()
                exchange.sendResponseHeaders(if (data != null && !fail) 200 else 503, bytes.size.toLong())
                exchange.responseBody.use { it.write(bytes) }
            }
            server.start()
            val props = ProxmoxProperties("http://127.0.0.1:${server.address.port}", "fixture", "sample", clusterId = "fixture-cluster")
            api = LocalGuestPlacementApi(GuestPlacement(PlacementInventoryAdapter(OkHttpClient(), props, ObjectMapper())))
        }

        private fun storageRows() =
            """[{"storage":"base","active":1,"enabled":1,"content":"images,vztmpl","shared":$shared},""" +
                """{"storage":"dest","active":$active,"enabled":1,"avail":$freeBytes,"content":"$targetContent","shared":0}]"""

        private fun metric(
            node: String,
            cpu: String,
        ) = """{"node":"$node","status":"online","cpu":${cpuField ?: cpu},"maxcpu":4,"maxmem":$memoryCapacity,"mem":100}"""

        fun preview(
            template: Template,
            team: Long = 1,
            alias: String = "standard",
            demand: Demand = Demand(1, 16, SOURCE_BYTES),
        ): PlacementDecision {
            val profile = PlacementProfile(aliases, "dest", template)
            val scope = AuthorizedPlacementScope(1, mapOf("standard" to profile), Demand(4, memoryCapacity, SOURCE_BYTES + 1), holds)
            return api.preview(PlacementQuery(team, alias, demand), scope)
        }

        override fun close() = server.stop(0)
    }
}
