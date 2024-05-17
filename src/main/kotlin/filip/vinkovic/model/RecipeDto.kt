package filip.vinkovic.model

import kotlinx.serialization.Serializable

@Serializable
data class RecipeDto(
    val id: Long,
    val name: String,
    val caloriesPerServing: Double,
    val proteinPerServing: Double,
    val carbsPerServing: Double,
    val fatPerServing: Double,
    val steps: String,
    val servings: Int,
    val ingredients: List<IngredientDto>
)

fun RecipeDto.toRecipeShortDto() = RecipeShortDto(id, name)