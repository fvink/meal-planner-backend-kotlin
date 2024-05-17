package filip.vinkovic.db.table

import org.jetbrains.exposed.sql.Table

object RecipeIngredients : Table(name = "recipe_ingredients") {
    val recipe = reference("recipe_id", Recipes)
    val ingredient = reference("ingredient_id", Ingredients)
    val amount = double("amount")
    val unit = varchar("unit", 255)
    val index = integer("index")

    override val primaryKey = PrimaryKey(recipe, ingredient)
}