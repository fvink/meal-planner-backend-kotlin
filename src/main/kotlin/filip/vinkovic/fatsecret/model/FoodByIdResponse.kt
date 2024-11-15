package filip.vinkovic.fatsecret.model

import kotlinx.serialization.Serializable

@Serializable
data class FoodByIdResponse(
    val food: Food
)
