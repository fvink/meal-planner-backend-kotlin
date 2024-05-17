package filip.vinkovic.model

import kotlinx.serialization.Serializable

@Serializable
data class CreateMealDto(
    val name: String,
    val mealTypeId: Long,
    val recipeIds: List<Long>
)
