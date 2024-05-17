package filip.vinkovic.model

import kotlinx.serialization.Serializable

@Serializable
data class MealTypeDto(
    val id: Long,
    val name: String
)
