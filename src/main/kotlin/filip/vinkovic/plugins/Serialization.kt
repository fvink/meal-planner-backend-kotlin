package filip.vinkovic.plugins

import filip.vinkovic.plugins.serializer.HttpStatusCodeSerializer
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            encodeDefaults = false
            serializersModule = SerializersModule {
                contextual(HttpStatusCode::class) { HttpStatusCodeSerializer }
            }
        })
        clearIgnoredTypes()
    }
    routing {
        get("/json/kotlinx-serialization") {
            call.respond(mapOf("hello" to "world"))
        }
    }
}
