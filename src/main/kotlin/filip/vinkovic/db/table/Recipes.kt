package filip.vinkovic.db.table

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable

object Recipes : LongIdTable() {
    val name = varchar("name", 255)
    val steps = varchar("steps", 255)
    val servings = integer("servings")
    val user = reference("user_id", Users)
}

class RecipeEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<RecipeEntity>(Recipes)

    var name by Recipes.name
    var steps by Recipes.steps
    var servings by Recipes.servings
//    var ingredients by IngredientEntity via RecipeIngredients
    var user by UserEntity referencedOn Recipes.user
}