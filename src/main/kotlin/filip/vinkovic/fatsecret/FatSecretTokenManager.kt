package filip.vinkovic.fatsecret

import filip.vinkovic.di.appModule
import filip.vinkovic.fatsecret.model.FSTokenDto
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.http.headers
import io.ktor.serialization.kotlinx.json.*
import kotlinx.io.IOException
import java.io.File
import java.util.concurrent.TimeUnit

class FatSecretTokenManager {

    private val httpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
        install(Logging) {
            level = LogLevel.ALL
        }
        install(Auth) {
            basic {
                sendWithoutRequest { true }
                credentials {
                    BasicAuthCredentials(
                        System.getenv("FATSECRET_CLIENT_ID"),
                        System.getenv("FATSECRET_CLIENT_SECRET")
                    )
                }
            }
        }
    }

    private var tokenDto: FSTokenDto? = null
    private var tokenExpirationTime: Long = 0

    suspend fun getAccessToken(): String? {
        if (tokenDto == null || tokenExpirationTime < System.currentTimeMillis()) {
            refreshAccessToken()
        }
        return tokenDto?.accessToken
    }

    suspend fun refreshAccessToken(): String? {
        try {
            println("Authorization: Basic ${System.getenv("FATSECRET_CLIENT_ID")}:${System.getenv("FATSECRET_CLIENT_SECRET")}")

            tokenDto = httpClient.submitForm(
                url = "$FAT_SECRET_BASE_URL/connect/token",
                formParameters = parameters {
                    append("grant_type", "client_credentials")
                    append("scope", "premier")
                }
            ).body()

            tokenExpirationTime = System.currentTimeMillis() + tokenDto!!.expiresIn * 1000
        } catch (e: Exception) {
            print("fatsecret token error: $e")
            tokenDto = null
        }
        return tokenDto?.accessToken
    }

    companion object {
        private const val FAT_SECRET_BASE_URL = "https://oauth.fatsecret.com"
    }
}