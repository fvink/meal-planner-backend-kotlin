package filip.vinkovic.db.table

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable

object MealPlans : LongIdTable(name = "meal_plans") {
    val name = varchar("name", 255)
}

class MealPlanEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<MealPlanEntity>(MealPlans)

    var name by MealPlans.name
    var meals by MealEntity via MealPlanMeals
}