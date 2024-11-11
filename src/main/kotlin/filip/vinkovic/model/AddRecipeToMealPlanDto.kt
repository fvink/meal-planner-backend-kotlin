package filip.vinkovic.model

import kotlinx.serialization.Serializable

@Serializable
data class AddRecipeToMealPlanDto(
    val dayAndMealTypes: List<DayAndMealTypes>,
    val recipeId: Long
)

@Serializable
data class DayAndMealTypes(
    val day: Int,
    val mealTypeIds: List<Long>
)