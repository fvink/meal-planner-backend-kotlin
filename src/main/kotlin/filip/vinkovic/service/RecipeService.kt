package filip.vinkovic.service

import filip.vinkovic.db.dao.RecipeDao
import filip.vinkovic.db.dao.UserDao
import filip.vinkovic.db.model.RecipeWithIngredientServings
import filip.vinkovic.fatsecret.FatSecretService
import filip.vinkovic.model.CreateRecipeDto
import filip.vinkovic.model.IngredientDto
import filip.vinkovic.model.IngredientWithAmountDto
import filip.vinkovic.model.RecipeDto
import filip.vinkovic.util.getUserIdForPrincipal
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Application.initializeRecipeService() {
    val recipeDao = RecipeDao()
    val userDao = UserDao()
    val fatSecretService by inject<FatSecretService>()

    routing {
        authenticate("auth-bearer") {
            get("/recipes") {
                val userId = call.getUserIdForPrincipal(userDao)
                if (userId == null) {
                    call.respond(HttpStatusCode.Unauthorized)
                    return@get
                }
                val recipes = recipeDao.readAll(userId, call.request.queryParameters["query"])
                call.respond(
                    HttpStatusCode.OK,
                    recipes.map { it.toRecipeDto(fatSecretService, loadIngredients = false) }
                )
            }

            get("/recipes/{id}") {
                val id = call.parameters["id"]?.toLong() ?: throw IllegalArgumentException("Invalid ID")
                try {
                    val recipe = recipeDao.read(id)
                    when (recipe == null) {
                        true -> call.respond(HttpStatusCode.NotFound)
                        false -> call.respond<RecipeDto>(HttpStatusCode.OK, recipe.toRecipeDto(fatSecretService))
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.NotFound)
                }
            }

            post("/recipes") {
                val userId = call.getUserIdForPrincipal(userDao)
                if (userId == null) {
                    call.respond(HttpStatusCode.Unauthorized)
                    return@post
                }
                val recipeData = call.receive<CreateRecipeDto>()
                if (recipeData.name.isBlank() || recipeData.ingredients.any { it.unit.isBlank() }) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }
                try {
                    val id = recipeDao.create(recipeData, userId)
                    val recipe = recipeDao.read(id)
                    when (recipe == null) {
                        true -> call.respond(HttpStatusCode.InternalServerError)
                        false -> call.respond<RecipeDto>(HttpStatusCode.OK, recipe.toRecipeDto(fatSecretService))
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError)
                }
            }

            put("/recipes/{id}") {
                val id = call.parameters["id"]?.toLong() ?: throw IllegalArgumentException("Invalid ID")
                val recipeData = call.receive<CreateRecipeDto>()
                recipeDao.update(id, recipeData)
                val recipe = recipeDao.read(id)
                when (recipe == null) {
                    true -> call.respond(HttpStatusCode.InternalServerError)
                    false -> call.respond<RecipeDto>(HttpStatusCode.OK, recipe.toRecipeDto(fatSecretService))
                }
            }

            delete("/recipes/{id}") {
                val id = call.parameters["id"]?.toLong() ?: throw IllegalArgumentException("Invalid ID")
                recipeDao.delete(id)
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}

suspend fun RecipeWithIngredientServings.toRecipeDto(
    fatSecretService: FatSecretService,
    loadIngredients: Boolean = true
): RecipeDto {
    val fsIngredientIds = ingredientServings.filter { it.ingredientSource == "fs" }.map { it.id }
    val ingredients = if (loadIngredients) fsIngredientIds.map { fatSecretService.getFoodById(it) } else emptyList()
    val ingredientAmounts = ingredientServings.map { serving ->
        IngredientWithAmountDto(
            ingredient = ingredients.find { it?.id == serving.id }
                ?: unknownIngredient, // TODO: handle unknown ingredient
            amount = serving.amount,
            unit = serving.unit,
            index = serving.index,
            fsServingId = serving.fsServingId,
            ingredientSource = serving.ingredientSource,
        )
    }
    return RecipeDto(
        id,
        name,
        0.0,
        0.0,
        0.0,
        0.0,
        steps,
        servings,
        ingredientAmounts
    )
}

val unknownIngredient = IngredientDto(0, "Unknown", "Unknown", "Unknown", emptyList())