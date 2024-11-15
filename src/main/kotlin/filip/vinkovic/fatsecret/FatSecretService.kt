package filip.vinkovic.fatsecret

import filip.vinkovic.fatsecret.model.Food
import filip.vinkovic.fatsecret.model.FoodAutocompleteDto
import filip.vinkovic.fatsecret.model.FoodByIdResponse
import filip.vinkovic.fatsecret.model.FoodsSearchResponse
import filip.vinkovic.model.IngredientDto
import filip.vinkovic.model.IngredientServingDto
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

private const val FAT_SECRET_BASE_URL = "https://platform.fatsecret.com/rest"

class FatSecretService(
    httpClientHolder: FatSecretHttpClientHolder
) {
    private val httpClient: HttpClient = httpClientHolder.get()

    suspend fun foodAutocomplete(query: String): List<String> {
        val response: FoodAutocompleteDto = httpClient.get("$FAT_SECRET_BASE_URL/food/autocomplete/v2") {
            parameter("expression", query)
            parameter("format", "json")
            parameter("max_results", 10)
        }.body()

        return response.data.suggestions
    }

    suspend fun foodSearch(query: String, maxResults: Int = 10): List<IngredientDto> {
        try {
            val response: FoodsSearchResponse = httpClient.get("$FAT_SECRET_BASE_URL/foods/search/v3") {
                parameter("search_expression", query)
                parameter("format", "json")
                parameter("max_results", maxResults)
                parameter("include_sub_categories", false)
                parameter("flag_default_serving", true)
            }.body()
            return response.foodsSearch?.results?.food?.map { it.toIngredientDto() } ?: emptyList()
        } catch (e: Exception) {
            println("foodSearch error: $e")
        }
        return emptyList()
    }

    suspend fun getFoodById(id: Long): IngredientDto? {
        try {
            val response: FoodByIdResponse = httpClient.get("$FAT_SECRET_BASE_URL/food/v4") {
                parameter("food_id", id)
                parameter("format", "json")
            }.body()
            return response.food.toIngredientDto()
        } catch (e: Exception) {
            println("getFoodById error: $e")
        }
        return null
    }
}

fun Food.toIngredientDto(): IngredientDto {
    return IngredientDto(
        id = this.foodId?.toLongOrNull() ?: 0L,
        name = this.foodName ?: "Unknown",
        description = this.brandName,
        source = "fs",
        servings = this.servings?.serving?.map {
            IngredientServingDto(
                servingId = it.servingId?.toLongOrNull() ?: 0L,
                servingDescription = it.servingDescription ?: "",
                servingUrl = it.servingUrl ?: "",
                metricServingAmount = it.metricServingAmount ?: "",
                metricServingUnit = it.metricServingUnit ?: "",
                numberOfUnits = it.numberOfUnits ?: "",
                measurementDescription = it.measurementDescription ?: "",
                isDefault = it.isDefault == "1",
                calories = it.calories ?: "",
                carbohydrate = it.carbohydrate ?: "",
                protein = it.protein ?: "",
                fat = it.fat ?: "",
                saturatedFat = it.saturatedFat ?: "",
                polyunsaturatedFat = it.polyunsaturatedFat ?: "",
                monounsaturatedFat = it.monounsaturatedFat ?: "",
                cholesterol = it.cholesterol ?: "",
                sodium = it.sodium ?: "",
                potassium = it.potassium ?: "",
                fiber = it.fiber ?: "",
                sugar = it.sugar ?: "",
                vitaminA = it.vitaminA ?: "",
                vitaminC = it.vitaminC ?: "",
                calcium = it.calcium ?: "",
                iron = it.iron ?: "",
            )
        } ?: emptyList()
    )
}