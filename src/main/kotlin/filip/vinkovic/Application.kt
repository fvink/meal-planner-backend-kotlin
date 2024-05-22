package filip.vinkovic

import filip.vinkovic.plugins.configureRouting
import filip.vinkovic.plugins.configureSerialization
import filip.vinkovic.service.initializeServices
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.cors.routing.*

fun main(args: Array<String>) {
    embeddedServer(
        Netty,
        port = System.getenv("SERVER_PORT").toInt(),
        host = System.getenv("SERVER_HOST"),
        module = Application::module
    ).start(wait = true)
}

fun Application.module() {
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
    configureSerialization()
    initializeServices()
    configureRouting()
}
