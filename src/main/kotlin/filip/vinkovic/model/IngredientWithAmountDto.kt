package filip.vinkovic.model

import kotlinx.serialization.Serializable

@Serializable
data class IngredientWithAmountDto(
    val ingredient: IngredientDto,
    val amount: Double,
    val unit: String,
    val index: Int,
    val fsServingId: Long?,
    val ingredientSource: String
)
