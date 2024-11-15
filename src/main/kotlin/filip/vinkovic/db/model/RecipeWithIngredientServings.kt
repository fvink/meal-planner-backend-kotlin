package filip.vinkovic.db.model

import kotlinx.serialization.Serializable

@Serializable
data class RecipeWithIngredientServings(
    val id: Long,
    val name: String,
    val steps: String,
    val servings: Int,
    val ingredientServings: List<IngredientServing>
)

@Serializable
data class IngredientServing(
    val id: Long,
    val amount: Double,
    val unit: String,
    val index: Int,
    val fsServingId: Long?,
    val ingredientSource: String
)