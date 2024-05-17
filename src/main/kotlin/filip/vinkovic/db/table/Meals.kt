package filip.vinkovic.db.table

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable

object Meals : LongIdTable() {
    val name = varchar("name", 255)
    val type = reference("meal_type_id", MealTypes)
}

class MealEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<MealEntity>(Meals)

    var name by Meals.name
    var type by MealTypeEntity referencedOn Meals.type
    var recipes by RecipeEntity via MealRecipes
}