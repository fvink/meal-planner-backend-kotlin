package filip.vinkovic.service

import filip.vinkovic.db.dao.IngredientDao
import filip.vinkovic.db.table.IngredientEntity
import filip.vinkovic.model.CreateIngredientDto
import filip.vinkovic.model.IngredientAmount
import filip.vinkovic.model.IngredientDto
import filip.vinkovic.util.convertUnit
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
            call.respond(HttpStatusCode.OK, ingredients.map { it.toDto() })
        }

        get("/ingredients/{id}") {
            val id = call.parameters["id"]?.toLong() ?: throw IllegalArgumentException("Invalid ID")
            try {
                val ingredient = ingredientDao.read(id)
                when (ingredient == null) {
                    true -> call.respond(HttpStatusCode.NotFound)
                    false -> call.respond<IngredientDto>(HttpStatusCode.OK, ingredient.toDto())
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

fun IngredientEntity.toDto(): IngredientDto {
    val (name, description) = this.name.splitIntoNameAndDescription()
    return IngredientDto(
        this.id.value,
        name,
        description,
        this.calories,
        this.protein,
        this.carbs,
        this.fat,
        this.weight_g,
        "g"
    )
}

// TODO: Implement support for volume_ml
fun IngredientEntity.toDtoScaled(amount: IngredientAmount): IngredientDto {
    val unitScale = convertUnit("g", amount.unit)
    val amountScale = amount.amount / (this.weight_g * unitScale)
    val (name, description) = this.name.splitIntoNameAndDescription()
    return IngredientDto(
        this.id.value,
        name,
        description,
        this.calories * amountScale,
        this.protein * amountScale,
        this.carbs * amountScale,
        this.fat * amountScale,
        amount.amount,
        amount.unit
    )
}

private fun String.splitIntoNameAndDescription() =
    this.split(",", limit = 2).let { it[0] to it.getOrElse(1) { "" }.trimStart() }