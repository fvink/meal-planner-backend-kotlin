package filip.vinkovic.service

import filip.vinkovic.db.dao.RecipeDao
import filip.vinkovic.db.dao.UserDao
import filip.vinkovic.db.table.IngredientEntity
import filip.vinkovic.db.table.RecipeEntity
import filip.vinkovic.model.CreateRecipeDto
import filip.vinkovic.model.IngredientAmount
import filip.vinkovic.model.RecipeDto
import filip.vinkovic.util.getUserIdForPrincipal
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.initializeRecipeService() {
    val recipeDao = RecipeDao()
    val userDao = UserDao()

    routing {
        authenticate("auth-bearer") {
            get("/recipes") {
                val userId = call.getUserIdForPrincipal(userDao)
                if (userId == null) {
                    call.respond(HttpStatusCode.Unauthorized)
                    return@get
                }
                recipeDao.readAll(userId, call.request.queryParameters["query"])
                call.respond(HttpStatusCode.OK, recipeDao.readAll(userId))
            }

            get("/recipes/{id}") {
                val id = call.parameters["id"]?.toLong() ?: throw IllegalArgumentException("Invalid ID")
                try {
                    val recipe = recipeDao.read(id)
                    when (recipe == null) {
                        true -> call.respond(HttpStatusCode.NotFound)
                        false -> call.respond<RecipeDto>(HttpStatusCode.OK, recipe)
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
                        false -> call.respond<RecipeDto>(HttpStatusCode.OK, recipe)
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
                    false -> call.respond<RecipeDto>(HttpStatusCode.OK, recipe)
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

fun RecipeEntity.toDto(ingredients: List<Pair<IngredientEntity, IngredientAmount>>): RecipeDto {
    val scaledIngredients = ingredients.map { (ingredient, amount) -> ingredient.toDtoScaled(amount) }
    val totalCalories = scaledIngredients.sumOf { ingredient -> ingredient.calories }
    val totalProtein = scaledIngredients.sumOf { ingredient -> ingredient.protein }
    val totalCarbs = scaledIngredients.sumOf { ingredient -> ingredient.carbs }
    val totalFat = scaledIngredients.sumOf { ingredient -> ingredient.fat }

    return RecipeDto(
        this.id.value,
        this.name,
        totalCalories / this.servings,
        totalProtein / this.servings,
        totalCarbs / this.servings,
        totalFat / this.servings,
        this.steps,
        this.servings,
        scaledIngredients
    )
}