package filip.vinkovic.util

import filip.vinkovic.db.dao.UserDao
import io.ktor.server.application.*
import io.ktor.server.auth.*

suspend fun ApplicationCall.getUserIdForPrincipal(userDao: UserDao): Long? {
    val principal = principal<UserIdPrincipal>()
    val email = principal?.name ?: return null
    return userDao.read(email)?.id?.value
}