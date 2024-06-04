package filip.vinkovic.service

import filip.vinkovic.db.dao.IngredientDao
import filip.vinkovic.model.CreateIngredientDto
import filip.vinkovic.model.IngredientDto
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.initializeIngredientService() {
    val ingredientDao = IngredientDao()

    routing {
        get("/ingredients") {
            val ingredients = if (call.request.queryParameters["name"] != null) {
                val name = call.request.queryParameters["name"]!!
                ingredientDao.read(name)
            } else {
                ingredientDao.readAll()
            }
            call.respond(HttpStatusCode.OK, ingredients
                .map {
                    IngredientDto(
                        it.id.value,
                        it.name,
                        it.calories,
                        it.protein,
                        it.carbs,
                        it.fat,
                        it.amount,
                        it.unit
                    )
                })
        }

        get("/ingredients/{id}") {
            val id = call.parameters["id"]?.toLong() ?: throw IllegalArgumentException("Invalid ID")
            try {
                val ingredient = ingredientDao.read(id)
                when (ingredient == null) {
                    true -> call.respond(HttpStatusCode.NotFound)
                    false -> call.respond<IngredientDto>(HttpStatusCode.OK, ingredient.let {
                        IngredientDto(
                            it.id.value,
                            it.name,
                            it.calories,
                            it.protein,
                            it.carbs,
                            it.fat,
                            it.amount,
                            it.unit
                        )
                    })
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        post("/ingredients") {
            val ingredient = call.receive<CreateIngredientDto>()
            val id = ingredientDao.create(ingredient)
            call.respond(HttpStatusCode.Created, id)
        }

        put("/ingredients/{id}") {
            val id = call.parameters["id"]?.toLong() ?: throw IllegalArgumentException("Invalid ID")
            val ingredient = call.receive<CreateIngredientDto>()
            ingredientDao.update(id, ingredient)
            call.respond(HttpStatusCode.OK)
        }

        delete("/ingredients/{id}") {
            val id = call.parameters["id"]?.toLong() ?: throw IllegalArgumentException("Invalid ID")
            ingredientDao.delete(id)
            call.respond(HttpStatusCode.OK)
        }
    }
}