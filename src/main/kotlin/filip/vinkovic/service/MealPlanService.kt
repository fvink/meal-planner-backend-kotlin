package filip.vinkovic.service

import filip.vinkovic.db.dao.MealDao
import filip.vinkovic.db.dao.MealPlanDao
import filip.vinkovic.db.dao.UserDao
import filip.vinkovic.model.AddMealToMealPlanDto
import filip.vinkovic.model.AddRecipeToMealPlanDto
import filip.vinkovic.model.CreateMealPlanDto
import filip.vinkovic.model.MealPlanDto
import filip.vinkovic.util.getUserIdForPrincipal
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.initializeMealPlanService() {
    val mealPlanDao = MealPlanDao(MealDao())
    val userDao = UserDao()

    routing {
        authenticate("auth-bearer") {
            get("/meal-plans") {
                val userId = call.getUserIdForPrincipal(userDao)
                if (userId == null) {
                    call.respond(HttpStatusCode.Unauthorized)
                    return@get
                }
                var mealPlans = mealPlanDao.readAll(userId)
                if (mealPlans.isEmpty()) {
                    mealPlanDao.create(CreateMealPlanDto("Meal Plan 1"), userId)
                    mealPlans = mealPlanDao.readAll(userId)
                }
                call.respond(HttpStatusCode.OK, mealPlans)
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
                val userId = call.getUserIdForPrincipal(userDao)
                if (userId == null) {
                    call.respond(HttpStatusCode.Unauthorized)
                    return@post
                }
                val mealPlan = call.receive<CreateMealPlanDto>()
                val id = mealPlanDao.create(mealPlan, userId)
                call.respond(HttpStatusCode.Created, id)
            }

            put("/meal-plans/{id}") {
                val id = call.parameters["id"]?.toLong() ?: throw IllegalArgumentException("Invalid ID")
                val mealPlan = call.receive<CreateMealPlanDto>()
                mealPlanDao.update(id, mealPlan)
                call.respond(HttpStatusCode.Created)
            }

            put("/meal-plans/{id}/add-recipe") {
                val id = call.parameters["id"]?.toLong() ?: throw IllegalArgumentException("Invalid ID")
                val data = call.receive<AddRecipeToMealPlanDto>()
                data.dayAndMealTypes.forEach { dayAndMealType ->
                    dayAndMealType.mealTypeIds.forEach { mealTypeId ->
                        mealPlanDao.addRecipe(id, dayAndMealType.day, mealTypeId, data.recipeId)
                    }
                }
                call.respond(HttpStatusCode.OK)
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

            delete("/meals/{mealId}/recipes/{recipeId}") {
                val mealId = call.parameters["mealId"]?.toLong() ?: throw IllegalArgumentException("Invalid meal ID")
                val recipeId =
                    call.parameters["recipeId"]?.toLong() ?: throw IllegalArgumentException("Invalid meal ID")
                mealPlanDao.removeRecipe(mealId, recipeId)
                call.respond(HttpStatusCode.OK)
            }

            delete("/meal-plans/{id}") {
                val id = call.parameters["id"]?.toLong() ?: throw IllegalArgumentException("Invalid ID")
                mealPlanDao.delete(id)
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}