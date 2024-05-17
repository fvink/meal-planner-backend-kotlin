package filip.vinkovic.db.table

import org.jetbrains.exposed.sql.Table

object MealPlanMeals : Table(name = "meal_plan_meals") {
    val mealPlan = reference("meal_plan_id", MealPlans)
    val meal = reference("meal_id", Meals)
    val day = integer("day")
    val mealType = reference("meal_type_id", MealTypes)

    override val primaryKey = PrimaryKey(mealPlan, meal)
}