package filip.vinkovic.model

import kotlinx.serialization.Serializable

@Serializable
data class CreateRecipeDto(
    val name: String,
    val steps: String,
    val servings: Int,
    val ingredients: List<CreateRecipeIngredientDto>
)

@Serializable
data class CreateRecipeIngredientDto(
    val id: Long,
    val amount: Double,
    val unit: String
)