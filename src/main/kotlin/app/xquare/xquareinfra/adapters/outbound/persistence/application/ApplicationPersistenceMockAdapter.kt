package app.xquare.xquareinfra.adapters.outbound.persistence.application

import app.xquare.xquareinfra.application.team.ports.outbound.ApplicationPersistencePort
import org.springframework.stereotype.Component

@Component
class ApplicationPersistenceMockAdapter : ApplicationPersistencePort {
    override fun deleteByTeamId(teamId: Long) {
    }
}
