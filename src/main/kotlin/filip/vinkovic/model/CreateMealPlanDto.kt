package filip.vinkovic.model

import kotlinx.serialization.Serializable

@Serializable
data class CreateMealPlanDto(
    val name: String
)
