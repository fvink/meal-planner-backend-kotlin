package filip.vinkovic.db.dao

import filip.vinkovic.db.model.IngredientServing
import filip.vinkovic.db.model.RecipeWithIngredientServings
import filip.vinkovic.db.table.RecipeEntity
import filip.vinkovic.db.table.RecipeIngredients
import filip.vinkovic.db.table.Recipes
import filip.vinkovic.db.table.UserEntity
import filip.vinkovic.model.CreateRecipeDto
import filip.vinkovic.model.CreateRecipeIngredientDto
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import kotlin.collections.set

class RecipeDao {

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    suspend fun create(recipe: CreateRecipeDto, userId: Long): Long = dbQuery {
        val recipeId = RecipeEntity.new {
            name = recipe.name
            steps = recipe.steps
            servings = recipe.servings
            user = UserEntity[userId]
        }.id.value
        batchInsertRecipeIngredients(recipeId, recipe.ingredients)
        recipeId
    }

    suspend fun update(id: Long, recipe: CreateRecipeDto) {
        dbQuery {
            RecipeIngredients.deleteWhere {
                RecipeIngredients.recipe eq id
            }
            batchInsertRecipeIngredients(id, recipe.ingredients)
            Recipes.update({ Recipes.id eq id }) { row ->
                row[name] = recipe.name
                row[steps] = recipe.steps
                row[servings] = recipe.servings
            }
        }
    }

    private fun batchInsertRecipeIngredients(recipeId: Long, ingredients: List<CreateRecipeIngredientDto>) {
        RecipeIngredients.batchInsert(ingredients.withIndex()) { ingredient ->
            this[RecipeIngredients.recipe] = EntityID(recipeId, Recipes)
            this[RecipeIngredients.ingredientId] = ingredient.value.ingredient.id
            this[RecipeIngredients.amount] = ingredient.value.amount
            this[RecipeIngredients.unit] = ingredient.value.unit
            this[RecipeIngredients.index] = ingredient.index
            this[RecipeIngredients.fsServingId] = ingredient.value.fsServingId
            this[RecipeIngredients.ingredientSource] = "fs" // TODO: handle other sources
        }
    }

    suspend fun readAll(userId: Long, query: String? = null): List<RecipeWithIngredientServings> {
        return dbQuery {
            val ingredientServings = mutableMapOf<Long, List<IngredientServing>>()
            val recipes = Recipes.selectAll()
                .where {
                    Recipes.user eq userId and
                            (if (query != null) Recipes.name.lowerCase() like "%${query.lowercase()}%" else Op.TRUE)
                }
                .toList()
            val recipeIds = recipes.map { it[Recipes.id] }
            RecipeIngredients.selectAll().where { RecipeIngredients.recipe.inList(recipeIds) }.forEach {
                val amount = it[RecipeIngredients.amount]
                val unit = it[RecipeIngredients.unit]
                val index = it[RecipeIngredients.index]
                val fsServingId = it[RecipeIngredients.fsServingId]
                val ingredientSource = it[RecipeIngredients.ingredientSource]
                val recipeId = it[RecipeIngredients.recipe].value
                ingredientServings[recipeId] = ingredientServings.getOrDefault(recipeId, emptyList()) +
                        IngredientServing(
                            it[RecipeIngredients.ingredientId],
                            amount,
                            unit,
                            index,
                            fsServingId,
                            ingredientSource
                        )
            }

            recipes.map {
                val recipeId = it[Recipes.id].value
                RecipeWithIngredientServings(
                    recipeId,
                    it[Recipes.name],
                    it[Recipes.steps],
                    it[Recipes.servings],
                    ingredientServings[recipeId] ?: emptyList()
                )
            }
        }
    }

    suspend fun read(id: Long): RecipeWithIngredientServings? {
        return dbQuery {
            Recipes.selectAll().where { Recipes.id eq id }
                .map { recipeRow ->
                    val recipe = RecipeEntity.wrap(recipeRow[Recipes.id], recipeRow)

                    val ingredientServings =
                        RecipeIngredients.selectAll().where { RecipeIngredients.recipe eq recipe.id }
                            .map {
                                val amount = it[RecipeIngredients.amount]
                                val unit = it[RecipeIngredients.unit]
                                val index = it[RecipeIngredients.index]
                                val fsServingId = it[RecipeIngredients.fsServingId]
                                val ingredientSource = it[RecipeIngredients.ingredientSource]
                                IngredientServing(
                                    it[RecipeIngredients.ingredientId],
                                    amount,
                                    unit,
                                    index,
                                    fsServingId,
                                    ingredientSource
                                )
                            }

                    RecipeWithIngredientServings(
                        recipe.id.value,
                        recipe.name,
                        recipe.steps,
                        recipe.servings,
                        ingredientServings
                    )
                }
                .singleOrNull()
        }
    }

    suspend fun delete(id: Long) {
        dbQuery {
            Recipes.deleteWhere { Recipes.id.eq(id) }
        }
    }
}
