package filip.vinkovic

import filip.vinkovic.di.appModule
import filip.vinkovic.plugins.configureRouting
import filip.vinkovic.plugins.configureSerialization
import filip.vinkovic.service.initializeServices
import filip.vinkovic.util.JwtDecoder
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.cors.routing.*
import org.koin.ktor.plugin.Koin

fun main(args: Array<String>) {
    embeddedServer(
        Netty,
        port = System.getenv("SERVER_PORT").toInt(),
        host = System.getenv("SERVER_HOST"),
        module = Application::module
    ).start(wait = true)
}

fun Application.module() {
    install(Koin) {
        modules(appModule)
    }

    install(CORS) {
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Delete)
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.AccessControlAllowOrigin)
        allowNonSimpleContentTypes = true
        allowCredentials = true
        anyHost()
    }
    install(Authentication) {
        bearer("auth-bearer") {
            realm = "Access to the '/' path"
            authenticate { tokenCredential ->
                val decodedJwt = JwtDecoder.decode(tokenCredential.token)
                val hasExpired = decodedJwt?.hasExpired() ?: true
                val email = decodedJwt?.email
                if (email != null && !hasExpired) {
                    UserIdPrincipal(email)
                } else {
                    null
                }
            }
        }
    }
    configureSerialization()
    initializeServices()
    configureRouting()
}
