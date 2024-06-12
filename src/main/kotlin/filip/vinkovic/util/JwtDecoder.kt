package filip.vinkovic.util

import io.ktor.util.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import java.time.Clock
import java.time.Instant

object JwtDecoder {

    @Throws(Exception::class)
    fun decode(encodedToken: String?): DecodedJwt? {
        if (encodedToken == null) {
            return null
        }
        val split: Array<String> = encodedToken.split(".").toTypedArray()

        if (split.size < 2) {
            return null
        }

        val json = Json {
            ignoreUnknownKeys = true
        }
        return json.decodeFromString(split[1].decodeBase64String())
    }
}

@Serializable
data class DecodedJwt(
    val email: String? = null,
    val exp: Long
) {
    fun hasExpired(): Boolean = Instant.ofEpochSecond(exp) < Clock.systemUTC().instant()
}