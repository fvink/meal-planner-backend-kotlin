package filip.vinkovic.model

import kotlinx.serialization.Serializable

@Serializable
data class AddMealToMealPlanDto(
    val mealId: Long,
    val day: Int,
    val mealTypeId: Long
)
