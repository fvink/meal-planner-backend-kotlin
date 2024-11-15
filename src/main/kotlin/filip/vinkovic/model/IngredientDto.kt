package filip.vinkovic.model

import kotlinx.serialization.Serializable

@Serializable
data class IngredientDto(
    val id: Long,
    val name: String,
    val description: String?,
    val source: String,
    val servings: List<IngredientServingDto>
)

@Serializable
data class IngredientServingDto(
    val servingId: Long,
    val servingDescription: String,
    val servingUrl: String,
    val metricServingAmount: String,
    val metricServingUnit: String,
    val numberOfUnits: String,
    val measurementDescription: String,
    val isDefault: Boolean,
    val calories: String,
    val carbohydrate: String,
    val protein: String,
    val fat: String,
    val saturatedFat: String,
    val polyunsaturatedFat: String,
    val monounsaturatedFat: String,
    val cholesterol: String,
    val sodium: String,
    val potassium: String,
    val fiber: String,
    val sugar: String,
    val vitaminA: String,
    val vitaminC: String,
    val calcium: String,
    val iron: String,
)