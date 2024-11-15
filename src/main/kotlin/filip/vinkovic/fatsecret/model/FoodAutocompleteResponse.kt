package filip.vinkovic.fatsecret.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FoodAutocompleteDto(
    @SerialName("suggestions") val data: Suggestions
)

@Serializable
data class Suggestions(
    @SerialName("suggestion") val suggestions: List<String>
)
