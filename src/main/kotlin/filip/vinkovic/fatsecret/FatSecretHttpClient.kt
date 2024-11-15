package filip.vinkovic.fatsecret

import filip.vinkovic.plugins.serializer.HttpStatusCodeSerializer
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule

class FatSecretHttpClientHolder(
    private val fatSecretTokenManager: FatSecretTokenManager
) {
    private val fatSecretHttpClient = HttpClient(CIO) {
        install(Logging) {
            level = LogLevel.ALL
        }
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                encodeDefaults = false
                ignoreUnknownKeys = true
                serializersModule = SerializersModule {
                    contextual(HttpStatusCode::class) { HttpStatusCodeSerializer }
                }
            })
        }
        install(Auth) {
            bearer {
                loadTokens {
                    val token = fatSecretTokenManager.getAccessToken()
                    if (token != null) {
                        BearerTokens(token, null)
                    } else {
                        null
                    }
                }
                refreshTokens {
                    val token = fatSecretTokenManager.refreshAccessToken()
                    if (token != null) {
                        BearerTokens(token, null)
                    } else {
                        null
                    }
                }
            }
        }
    }

    fun get(): HttpClient {
        return fatSecretHttpClient
    }
}
