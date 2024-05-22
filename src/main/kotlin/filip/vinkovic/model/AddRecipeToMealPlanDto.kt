package filip.vinkovic.model

import kotlinx.serialization.Serializable

@Serializable
data class AddRecipeToMealPlanDto(
    val day: Int,
    val mealTypeId: Long,
    val recipeId: Long
)
