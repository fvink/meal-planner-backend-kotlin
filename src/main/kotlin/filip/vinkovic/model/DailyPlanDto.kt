package filip.vinkovic.model

import kotlinx.serialization.Serializable

@Serializable
data class DailyPlanDto(
    val day: Int,
    val meals: List<MealDto>,
    val calories: Double,
    val protein: Double,
    val carbs: Double,
    val fat: Double
)
