package app.xquare.xquareinfra.application.user

import app.xquare.xquareinfra.application.team.UserException
import app.xquare.xquareinfra.application.user.ports.inbound.*
import app.xquare.xquareinfra.application.user.ports.outbound.UserPersistenceForUserPort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class UserService(
    private val userPersistencePort: UserPersistenceForUserPort,
) : GetUserUseCase,
    SearchUsersUseCase {
    override fun getUser(query: GetUserQuery): GetUserResult {
        val user =
            userPersistencePort.findById(query.userId)
                ?: throw UserException.UserNotFound

        return GetUserResult(user)
    }

    override fun searchUsers(query: SearchUsersQuery): SearchUsersResult {
        val usersByName = query.name?.let { userPersistencePort.listByNameContaining(query.name) }.orEmpty()
        val usersByEmail = query.email?.let { userPersistencePort.listByEmailContaining(query.email) }.orEmpty()

        val users =
            (usersByName + usersByEmail)
                .associateBy { it.id }
                .values
                .toList()

        return SearchUsersResult(users)
    }
}
