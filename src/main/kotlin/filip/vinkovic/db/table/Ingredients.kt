package filip.vinkovic.db.table

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable

object Ingredients : LongIdTable() {
    val name = varchar("name", 255)
    val calories = double("calories")
    val protein = double("protein")
    val carbs = double("carbs")
    val fat = double("fat")
    val amount = double("amount")
    val unit = varchar("unit", 255)
}

class IngredientEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<IngredientEntity>(Ingredients)

    var name by Ingredients.name
    var calories by Ingredients.calories
    var protein by Ingredients.protein
    var carbs by Ingredients.carbs
    var fat by Ingredients.fat
    var amount by Ingredients.amount
    var unit by Ingredients.unit
}