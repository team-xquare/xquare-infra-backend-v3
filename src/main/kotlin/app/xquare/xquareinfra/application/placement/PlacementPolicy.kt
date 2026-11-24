package app.xquare.xquareinfra.application.placement

import java.time.Duration
import java.time.Instant

data class Demand(
    val cores: Int,
    val memoryBytes: Long,
    val diskBytes: Long,
) {
    fun valid() = cores > 0 && memoryBytes > 0 && diskBytes > 0
}

data class Compute(
    val cores: Int,
    val memoryBytes: Long,
)

/** Totals not yet reflected by the observations. Shared disks use one pool key across nodes. */
data class NodeKey(
    val cluster: String,
    val node: String,
)

data class PoolKey(
    val cluster: String,
    val storage: String,
    val localNode: String?,
)

data class Reservations(
    val byNode: Map<NodeKey, Compute>,
    val diskByPool: Map<PoolKey, Long>,
)

data class Candidate(
    val alias: String,
    val cores: Int,
    val cpuUsed: Double,
    val memoryTotal: Long,
    val memoryUsed: Long,
    val diskAvailable: Long,
    val diskPool: PoolKey,
    val physicalNode: NodeKey,
    val minFinalDiskBytes: Long,
)

data class Inventory(
    val sampledAt: Instant,
    val candidates: List<Candidate>,
)

object PlacementPolicy {
    val maxAge: Duration = Duration.ofSeconds(30)

    fun choose(
        inventory: Inventory,
        demand: Demand,
        reservations: Reservations,
        now: Instant,
    ): String? {
        val age = Duration.between(inventory.sampledAt, now)
        val nodes = inventory.candidates
        if (!demand.valid() || age.isNegative || age > maxAge || nodes.distinctBy { it.alias }.size != nodes.size) return null
        if (reservations.byNode.values.any { it.cores < 0 || it.memoryBytes < 0 } ||
            reservations.diskByPool.values.any { it < 0 }
        ) {
            return null
        }
        val poolKeys = nodes.map { it.diskPool } + reservations.diskByPool.filterValues { it > 0 }.keys
        val shared = poolKeys.filter { it.localNode == null }.toSet()
        val ambiguous = poolKeys.filter { it.localNode != null }.map { it.copy(localNode = null) }.intersect(shared)
        return nodes
            .mapNotNull { node ->
                if (node.diskPool.copy(localNode = null) in ambiguous) return@mapNotNull null
                val held = reservations.byNode[node.physicalNode] ?: Compute(0, 0)
                val diskHeld = reservations.diskByPool[node.diskPool] ?: 0
                score(node, demand, held, diskHeld)?.let { it to node.alias }
            }.minWithOrNull(compareBy<Pair<Double, String>> { it.first }.thenBy { it.second })
            ?.second
    }

    private fun score(
        node: Candidate,
        demand: Demand,
        held: Compute,
        diskHeld: Long,
    ): Double? {
        if (node.alias.isBlank() ||
            node.diskPool.storage.isBlank() ||
            node.physicalNode.node.isBlank() ||
            node.physicalNode.cluster.isBlank() ||
            node.diskPool.cluster != node.physicalNode.cluster ||
            (node.diskPool.localNode != null && node.diskPool.localNode != node.physicalNode.node) ||
            node.cores <= 0 ||
            node.memoryTotal <= 0
        ) {
            return null
        }
        if (node.cpuUsed !in 0.0..1.0 || node.memoryUsed !in 0..node.memoryTotal || node.diskAvailable < 0) return null
        val freeMemory = node.memoryTotal - node.memoryUsed
        if (held.memoryBytes > freeMemory || demand.memoryBytes > freeMemory - held.memoryBytes) return null
        if (node.minFinalDiskBytes < 0 || demand.diskBytes < node.minFinalDiskBytes) return null
        if (diskHeld > node.diskAvailable || demand.diskBytes > node.diskAvailable - diskHeld) return null
        val projectedCpu = node.cpuUsed + (demand.cores.toLong() + held.cores).toDouble() / node.cores
        if (projectedCpu > 1.0) return null
        val projectedRam = (node.memoryUsed + held.memoryBytes + demand.memoryBytes).toDouble() / node.memoryTotal
        return 0.4 * projectedCpu + 0.6 * projectedRam
    }
}
