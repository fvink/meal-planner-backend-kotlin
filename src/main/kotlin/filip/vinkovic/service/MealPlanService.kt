package filip.vinkovic.service

import filip.vinkovic.db.dao.MealPlanDao
import filip.vinkovic.model.AddMealToMealPlanDto
import filip.vinkovic.model.CreateMealPlanDto
import filip.vinkovic.model.MealPlanDto
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.initializeMealPlanService() {
    val mealPlanDao = MealPlanDao()

    routing {
        get("/meal-plans") {
            call.respond(HttpStatusCode.OK, mealPlanDao.readAll())
        }

        get("/meal-plans/{id}") {
            val id = call.parameters["id"]?.toLong() ?: throw IllegalArgumentException("Invalid ID")
            try {
                val mealPlan = mealPlanDao.read(id)
                when (mealPlan == null) {
                    true -> call.respond(HttpStatusCode.NotFound)
                    false -> call.respond<MealPlanDto>(HttpStatusCode.OK, mealPlan)
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        post("/meal-plans") {
            val mealPlan = call.receive<CreateMealPlanDto>()
            val id = mealPlanDao.create(mealPlan)
            call.respond(HttpStatusCode.Created, id)
        }

        put("/meal-plans/{id}/meals") {
            val id = call.parameters["id"]?.toLong() ?: throw IllegalArgumentException("Invalid ID")
            val meal = call.receive<AddMealToMealPlanDto>()
            mealPlanDao.addMeal(id, meal.day, meal.mealId, meal.mealTypeId)
            call.respond(HttpStatusCode.OK)
        }

        delete("/meal-plans/{id}/day/{day}/meals/{mealId}") {
            val id = call.parameters["id"]?.toLong() ?: throw IllegalArgumentException("Invalid meal plan ID")
            val day = call.parameters["day"]?.toInt() ?: throw IllegalArgumentException("Invalid day param")
            val mealId = call.parameters["day"]?.toLong() ?: throw IllegalArgumentException("Invalid meal ID")
            mealPlanDao.removeMeal(id, day, mealId)
            call.respond(HttpStatusCode.OK)
        }

        delete("/meal-plans/{id}") {
            val id = call.parameters["id"]?.toLong() ?: throw IllegalArgumentException("Invalid ID")
            mealPlanDao.delete(id)
            call.respond(HttpStatusCode.OK)
        }
    }
}