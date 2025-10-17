package app.xquare.xquareinfra.domain.user

data class User(
    val id: Long? = null,
    val username: String,
    val password: String,
    val role: UserRole,
    val studentNumber: Int,
    val name: String,
    val email: String,
)
