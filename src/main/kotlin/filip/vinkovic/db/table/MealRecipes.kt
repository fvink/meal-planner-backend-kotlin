package filip.vinkovic.db.table

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object MealRecipes : Table(name = "meal_recipes") {
    val meal = reference("meal_id", Meals, onDelete = ReferenceOption.CASCADE)
    val recipe = reference("recipe_id", Recipes, onDelete = ReferenceOption.CASCADE)
    val index = integer("index")

    override val primaryKey = PrimaryKey(meal, recipe)
}