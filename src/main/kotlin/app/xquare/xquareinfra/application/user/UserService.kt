package app.xquare.xquareinfra.application.user

import app.xquare.xquareinfra.application.user.ports.inbound.GetUserQuery
import app.xquare.xquareinfra.application.user.ports.inbound.GetUserResult
import app.xquare.xquareinfra.application.user.ports.inbound.GetUserUseCase
import app.xquare.xquareinfra.application.user.ports.outbound.UserPersistenceForUserPort
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userPersistencePort: UserPersistenceForUserPort,
) : GetUserUseCase {
    override fun getUser(query: GetUserQuery): GetUserResult {
        val user =
            userPersistencePort.findById(query.userId)
                ?: return GetUserResult.UserNotExists

        return GetUserResult.Success(user)
    }
}
