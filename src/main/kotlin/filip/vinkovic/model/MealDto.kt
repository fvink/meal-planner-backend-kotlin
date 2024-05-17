package filip.vinkovic.model

import kotlinx.serialization.Serializable

@Serializable
data class MealDto(
    val id: Long,
    val name: String,
    val type: MealTypeDto,
    val recipes: List<RecipeShortDto>
)
