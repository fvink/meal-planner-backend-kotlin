package filip.vinkovic.model

import kotlinx.serialization.Serializable

@Serializable
data class RecipeShortDto(
    val id: Long,
    val name: String,
)