package filip.vinkovic.fatsecret.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FSTokenDto(
    @SerialName("access_token")
    val accessToken: String,

    @SerialName("expires_in")
    val expiresIn: Int,

    @SerialName("token_type")
    val tokenType: String,

    @SerialName("scope")
    val scope: String
)

