package app.xquare.xquareinfra.application.team.ports.outbound

interface ApplicationPersistencePort {
    fun deleteByTeamId(teamId: Long)
}
