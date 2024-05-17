package filip.vinkovic

import filip.vinkovic.plugins.*
import filip.vinkovic.service.initializeServices
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main(args: Array<String>) {
    embeddedServer(
        Netty,
        port = System.getenv("SERVER_PORT").toInt(),
        host =  System.getenv("SERVER_HOST"),
        module = Application::module
    ).start(wait = true)
}

fun Application.module() {
    configureSerialization()
    initializeServices()
    configureRouting()
}
