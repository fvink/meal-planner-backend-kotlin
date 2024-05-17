package filip.vinkovic.db.dao

import filip.vinkovic.db.table.*
import filip.vinkovic.model.CreateRecipeDto
import filip.vinkovic.model.IngredientAmount
import filip.vinkovic.model.RecipeDto
import filip.vinkovic.service.toDto
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import kotlin.collections.set

class RecipeDao {

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    suspend fun create(recipe: CreateRecipeDto): Long = dbQuery {
        val recipeId = RecipeEntity.new {
            name = recipe.name
            steps = recipe.steps
        }.id.value

        RecipeIngredients.batchInsert(recipe.ingredients.withIndex()) { ingredient ->
            this[RecipeIngredients.recipe] = EntityID(recipeId, Recipes)
            this[RecipeIngredients.ingredient] = EntityID(ingredient.value.id, Ingredients)
            this[RecipeIngredients.amount] = ingredient.value.amount
            this[RecipeIngredients.unit] = ingredient.value.unit
            this[RecipeIngredients.index] = ingredient.index
        }

        recipeId
    }

    suspend fun readAll(): List<RecipeDto> {
        return dbQuery {
            val ingredientAmounts = mutableMapOf<RecipeEntity, List<Pair<IngredientEntity, IngredientAmount>>>()

            RecipeIngredients.selectAll().forEach {
                val amount = it[RecipeIngredients.amount]
                val unit = it[RecipeIngredients.unit]
                val recipe = RecipeEntity.wrap(it[RecipeIngredients.recipe], it)
                ingredientAmounts[recipe] = ingredientAmounts.getOrDefault(recipe, emptyList()) + Pair(
                    IngredientEntity.wrap(it[RecipeIngredients.ingredient], it),
                    IngredientAmount(it[RecipeIngredients.ingredient].value, amount, unit)
                )
            }

            RecipeEntity.all().map {
                it.toDto(ingredientAmounts[it] ?: emptyList())
            }
        }
    }

    suspend fun read(id: Long): RecipeDto? {
        return dbQuery {
            Recipes.selectAll().where { Recipes.id eq id }
                .map { recipeRow ->
                    val recipe = RecipeEntity.wrap(recipeRow[Recipes.id], recipeRow)

                    val ingredientAmounts =
                        RecipeIngredients.selectAll().where { RecipeIngredients.recipe eq recipe.id }
                            .map {
                                val amount = it[RecipeIngredients.amount]
                                val unit = it[RecipeIngredients.unit]
                                val ingredient = IngredientEntity.wrap(it[RecipeIngredients.ingredient], it)
                                Pair(ingredient, IngredientAmount(ingredient.id.value, amount, unit))
                            }

                    recipe.toDto(ingredientAmounts)
                }
                .singleOrNull()
        }
    }

    suspend fun update(id: Long, recipe: CreateRecipeDto) {
        dbQuery {
            // TODO Update ingredients
            Recipes.update({ Recipes.id eq id }) { row ->
                row[name] = recipe.name
                row[steps] = recipe.steps
            }
        }
    }

    suspend fun delete(id: Long) {
        dbQuery {
            Ingredients.deleteWhere { Ingredients.id.eq(id) }
        }
    }
}
