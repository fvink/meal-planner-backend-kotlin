package filip.vinkovic.service

import filip.vinkovic.db.dao.UserDao
import filip.vinkovic.model.GoogleAuthDto
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.initializeAuthService() {
    val userDao = UserDao()

    routing {
        post("/auth/google") {
            try {
                val data = call.receive<GoogleAuthDto>()
                val userEntity = userDao.read(data.email) ?: userDao.createUser(data.email, "user")
                if (userEntity != null) {
                    call.respond(HttpStatusCode.Created)
                } else {
                    call.respond(HttpStatusCode.InternalServerError)
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError)
            }
        }
    }
}