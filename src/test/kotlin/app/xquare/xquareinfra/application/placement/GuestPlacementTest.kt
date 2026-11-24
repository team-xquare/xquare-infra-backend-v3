package app.xquare.xquareinfra.application.placement

import org.junit.jupiter.api.Test
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GuestPlacementTest {
    private val now = Instant.parse("2026-01-01T00:00:00Z")
    private val request = Demand(1, 16, 8)
    private val node = Candidate("a", 4, 0.25, 100, 20, 40, PoolKey("cluster", "shared-pool", null), NodeKey("cluster", "n1"), 0)

    private fun choose(
        nodes: List<Candidate>,
        reservations: Reservations = Reservations(emptyMap(), emptyMap()),
        sampledAt: Instant = now,
        demand: Demand = request,
    ) = PlacementPolicy.choose(Inventory(sampledAt, nodes), demand, reservations, now)

    @Test
    fun `uses projected load including requested cores and memory`() {
        val small = node.copy(cpuUsed = 0.0, memoryUsed = 0, cores = 1, memoryTotal = 16)
        assertEquals("b", choose(listOf(small, node.copy(alias = "b", physicalNode = NodeKey("cluster", "n2")))))
    }

    @Test
    fun `includes unobserved reservations in score and every capacity bound`() {
        val b = node.copy(alias = "b", physicalNode = NodeKey("cluster", "n2"))
        assertEquals("b", choose(listOf(node, b), Reservations(mapOf(node.physicalNode to Compute(1, 12)), emptyMap())))
        for (held in listOf(Compute(4, 0), Compute(0, 65), Compute(-1, 0), Compute(0, Long.MAX_VALUE))) {
            assertNull(choose(listOf(node), Reservations(mapOf(node.physicalNode to held), emptyMap())))
        }
        assertNull(choose(listOf(node, b), Reservations(emptyMap(), mapOf(node.diskPool to 33L))))
    }

    @Test
    fun `rejects empty stale future duplicate and invalid observations`() {
        assertNull(choose(emptyList()))
        assertNull(choose(listOf(node), sampledAt = now.minusSeconds(31)))
        assertNull(choose(listOf(node), sampledAt = now.plusSeconds(1)))
        assertNull(choose(listOf(node, node)))
        for (bad in listOf(
            node.copy(cpuUsed = Double.NaN),
            node.copy(cpuUsed = 1.1),
            node.copy(memoryUsed = -1),
            node.copy(cores = 0),
            node.copy(diskAvailable = -1),
        )) {
            assertNull(choose(listOf(bad)))
        }
        assertNull(choose(listOf(node), demand = request.copy(diskBytes = 0)))
    }

    @Test
    fun `reservations on an equally named node in a different cluster do not consume capacity`() {
        val elsewhere = NodeKey("another-cluster", "n1")
        assertEquals("a", choose(listOf(node), Reservations(mapOf(elsewhere to Compute(4, 100)), emptyMap())))
    }

    @Test
    fun `ties use a stable server alias regardless of enumeration order`() {
        val b = node.copy(alias = "b", physicalNode = NodeKey("cluster", "n2"))
        assertEquals("a", choose(listOf(b, node)))
        assertEquals("a", choose(listOf(node, b)))
    }
}
