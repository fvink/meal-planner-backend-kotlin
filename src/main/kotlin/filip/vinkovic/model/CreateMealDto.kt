package filip.vinkovic.model

import kotlinx.serialization.Serializable

@Serializable
data class CreateMealDto(
    val mealTypeId: Long,
    val recipeIds: List<Long>,
    val name: String?
)
