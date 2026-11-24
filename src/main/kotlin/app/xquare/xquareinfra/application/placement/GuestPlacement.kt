package app.xquare.xquareinfra.application.placement

import org.springframework.stereotype.Service
import java.time.Instant

/** Server-resolved inputs, never bind these types to a tenant request. Aliases are opaque catalog IDs. */
data class PlacementProfile(
    val nodes: Map<String, String>,
    val targetStore: String,
    val template: Template,
) {
    fun valid(): Boolean {
        val id = Regex("[A-Za-z0-9][A-Za-z0-9_.-]{0,63}")
        return nodes.size in 1..32 &&
            nodes.values.distinct().size == nodes.size &&
            nodes.all { id.matches(it.key) && id.matches(it.value) } &&
            id.matches(targetStore) &&
            when (template) {
                is Template.Qemu ->
                    template.vmid in 100..999999999 &&
                        template.sourceNode in nodes.values &&
                        template.sourceStores.isNotEmpty() &&
                        template.sourceStores.all(id::matches)
                is Template.Lxc ->
                    template.sourceNode in nodes.values &&
                        id.matches(template.store) &&
                        template.volume.startsWith("${template.store}:vztmpl/") &&
                        template.volume.substringAfter(":vztmpl/").matches(Regex("[A-Za-z0-9][A-Za-z0-9._-]{0,254}"))
            }
    }
}

sealed interface Template {
    /** Operator-approved backing storage set for this template revision, not tenant-supplied. */
    data class Qemu(
        val sourceNode: String,
        val vmid: Int,
        val sourceStores: Set<String>,
    ) : Template

    data class Lxc(
        val sourceNode: String,
        val store: String,
        val volume: String,
    ) : Template
}

fun interface PlacementInventory {
    fun read(profile: PlacementProfile): Inventory?
}

sealed interface PlacementDecision {
    data class Selected(
        val alias: String,
    ) : PlacementDecision

    object Unavailable : PlacementDecision
}

/** In-process API for the future authenticated create flow. Does not allocate or persist a reservation. */
@Service
class GuestPlacement(
    private val inventory: PlacementInventory,
) {
    fun preview(
        profile: PlacementProfile,
        demand: Demand,
        reservations: Reservations,
    ): PlacementDecision {
        if (!profile.valid() || !demand.valid()) return PlacementDecision.Unavailable
        val observation = inventory.read(profile) ?: return PlacementDecision.Unavailable
        val alias = PlacementPolicy.choose(observation, demand, reservations, Instant.now()) ?: return PlacementDecision.Unavailable
        return PlacementDecision.Selected(alias)
    }
}
