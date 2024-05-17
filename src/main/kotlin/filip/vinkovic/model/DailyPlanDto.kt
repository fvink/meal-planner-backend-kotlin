package filip.vinkovic.model

import kotlinx.serialization.Serializable

@Serializable
data class DailyPlanDto(
    val day: Int,
    val meals: List<MealDto>
)
