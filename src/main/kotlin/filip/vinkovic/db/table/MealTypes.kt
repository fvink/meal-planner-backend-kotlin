package filip.vinkovic.db.table

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable

object MealTypes : LongIdTable("meal_types") {
    val name = varchar("name", 255)
}

class MealTypeEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<MealTypeEntity>(MealTypes)

    var name by MealTypes.name
}