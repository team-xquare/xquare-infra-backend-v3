package app.xquare.xquareinfra.application.team.ports.outbound

import app.xquare.xquareinfra.domain.team.Team

interface TeamPersistenceForTeamPort {
    fun existsByName(name: String): Boolean

    fun listByUserId(userId: Long): List<Team>

    fun findById(teamId: Long): Team?

    fun save(team: Team): Team

    fun delete(teamId: Long)
}
