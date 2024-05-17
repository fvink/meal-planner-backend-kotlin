package filip.vinkovic.service

import filip.vinkovic.db.dao.MealDao
import filip.vinkovic.db.dao.RecipeDao
import filip.vinkovic.model.CreateMealDto
import filip.vinkovic.model.MealDto
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.initializeMealService() {
    val mealDao = MealDao()
    val recipeDao = RecipeDao()

    routing {
        get("/meals") {
            call.respond(HttpStatusCode.OK, mealDao.readAll())
        }

        get("/meals/{id}") {
            val id = call.parameters["id"]?.toLong() ?: throw IllegalArgumentException("Invalid ID")
            try {
                val meal = mealDao.read(id)
                when (meal == null) {
                    true -> call.respond(HttpStatusCode.NotFound)
                    false -> call.respond<MealDto>(HttpStatusCode.OK, meal)
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        post("/meals") {
            val meal = call.receive<CreateMealDto>()
            val id = mealDao.create(meal)
            call.respond(HttpStatusCode.Created, id)
        }

        put("/meals/{id}") {
            val id = call.parameters["id"]?.toLong() ?: throw IllegalArgumentException("Invalid ID")
            val meal = call.receive<CreateMealDto>()
            mealDao.update(id, meal)
            call.respond(HttpStatusCode.OK)
        }

        delete("/meals/{id}") {
            val id = call.parameters["id"]?.toLong() ?: throw IllegalArgumentException("Invalid ID")
            mealDao.delete(id)
            call.respond(HttpStatusCode.OK)
        }
    }
}