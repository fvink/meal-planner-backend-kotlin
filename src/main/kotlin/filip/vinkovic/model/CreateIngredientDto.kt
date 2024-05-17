package filip.vinkovic.model

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable

@Serializable
data class CreateIngredientDto @OptIn(ExperimentalSerializationApi::class) constructor(
    @EncodeDefault val name: String? = null,
    @EncodeDefault val calories: Double? = null,
    @EncodeDefault val protein: Double? = null,
    @EncodeDefault val carbs: Double? = null,
    @EncodeDefault val fat: Double? = null,
    @EncodeDefault val amount: Double? = null,
    @EncodeDefault val unit: String? = null
)