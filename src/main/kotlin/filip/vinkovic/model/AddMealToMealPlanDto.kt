package filip.vinkovic.model

data class AddMealToMealPlanDto(
    val mealId: Long,
    val day: Int,
    val mealTypeId: Long
)
