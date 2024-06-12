package filip.vinkovic.model

import kotlinx.serialization.Serializable

@Serializable
data class GoogleAuthDto(
    val idToken: String,
    val email: String,
)
