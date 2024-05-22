package filip.vinkovic.service

import filip.vinkovic.db.dao.MealTypeDao
import filip.vinkovic.db.table.MealTypeEntity
import filip.vinkovic.model.CreateMealTypeDto
import filip.vinkovic.model.MealTypeDto
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.initializeMealTypeService() {
    val mealTypeDao = MealTypeDao()

    routing {
        get("/meal-types") {
            call.respond(HttpStatusCode.OK, mealTypeDao.readAll().map { it.toDto() })
        }

        get("/meals-types/{id}") {
            val id = call.parameters["id"]?.toLong() ?: throw IllegalArgumentException("Invalid ID")
            try {
                val mealType = mealTypeDao.read(id)
                when (mealType == null) {
                    true -> call.respond(HttpStatusCode.NotFound)
                    false -> call.respond<MealTypeDto>(HttpStatusCode.OK, mealType.toDto())
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        post("/meals-types") {
            val mealType = call.receive<CreateMealTypeDto>()
            val id = mealTypeDao.create(mealType)
            call.respond(HttpStatusCode.Created, id)
        }

        put("/meals-types/{id}") {
            val id = call.parameters["id"]?.toLong() ?: throw IllegalArgumentException("Invalid ID")
            val mealType = call.receive<CreateMealTypeDto>()
            mealTypeDao.update(id, mealType)
            call.respond(HttpStatusCode.OK)
        }

        delete("/meals-types/{id}") {
            val id = call.parameters["id"]?.toLong() ?: throw IllegalArgumentException("Invalid ID")
            mealTypeDao.delete(id)
            call.respond(HttpStatusCode.OK)
        }
    }
}

private fun MealTypeEntity.toDto() = MealTypeDto(id.value, name, index)