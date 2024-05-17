package filip.vinkovic.model

import kotlinx.serialization.Serializable

@Serializable
data class MealPlanDto(
    val id: Long,
    val name: String,
    val dailyPlans: List<DailyPlanDto>
)
