package filip.vinkovic.db.table

import org.jetbrains.exposed.sql.Table

object RecipeIngredients : Table(name = "recipe_ingredients") {
    val recipe = reference("recipe_id", Recipes)
    val ingredientId = long("ingredient_id")
    val amount = double("amount")
    val unit = varchar("unit", 255)
    val index = integer("index")
    val fsServingId = long("fs_serving_id").nullable()
    val ingredientSource = varchar("ingredient_source", 255)

    override val primaryKey = PrimaryKey(recipe, ingredientId)
}