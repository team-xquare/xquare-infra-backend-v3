package app.xquare.xquareinfra.adapters.inbound.local.placement

import app.xquare.xquareinfra.application.placement.Demand
import app.xquare.xquareinfra.application.placement.GuestPlacement
import app.xquare.xquareinfra.application.placement.PlacementDecision
import app.xquare.xquareinfra.application.placement.PlacementProfile
import app.xquare.xquareinfra.application.placement.Reservations
import org.springframework.stereotype.Component

/** Supplied by trusted server code after team authorization; never deserialize this from a request. */
data class AuthorizedPlacementScope(
    val teamId: Long,
    val profiles: Map<String, PlacementProfile>,
    val remainingQuota: Demand,
    val unobserved: Reservations,
)

data class PlacementQuery(
    val teamId: Long,
    val templateAlias: String,
    val demand: Demand,
)

/** Local (not HTTP) boundary: callers can select a catalog alias, never a VMID, store, node or path. */
@Component
class LocalGuestPlacementApi(
    private val placement: GuestPlacement,
) {
    fun preview(
        query: PlacementQuery,
        scope: AuthorizedPlacementScope?,
    ): PlacementDecision {
        if (scope == null || query.teamId <= 0 || query.teamId != scope.teamId) return PlacementDecision.Unavailable
        val profile = scope.profiles[query.templateAlias] ?: return PlacementDecision.Unavailable
        val requested = query.demand
        val quota = scope.remainingQuota
        if (!requested.valid() ||
            requested.cores > quota.cores ||
            requested.memoryBytes > quota.memoryBytes ||
            requested.diskBytes > quota.diskBytes
        ) {
            return PlacementDecision.Unavailable
        }
        return placement.preview(profile, requested, scope.unobserved)
    }
}
