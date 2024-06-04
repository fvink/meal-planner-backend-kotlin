package filip.vinkovic.db.dao

import filip.vinkovic.db.table.*
import filip.vinkovic.model.CreateMealDto
import filip.vinkovic.model.MealDto
import filip.vinkovic.model.MealTypeDto
import filip.vinkovic.model.RecipeShortDto
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.load
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class MealDao {

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    suspend fun create(meal: CreateMealDto): Long = dbQuery {
        val mealId = MealEntity.new {
            name = meal.name ?: ""
            type = MealTypeEntity.findById(meal.mealTypeId)?.load() ?: MealTypeEntity[1] // default to Breakfast
        }.id.value

        MealRecipes.batchInsert(meal.recipeIds.withIndex()) { recipeId ->
            this[MealRecipes.meal] = EntityID(mealId, Meals)
            this[MealRecipes.recipe] = EntityID(recipeId.value, Recipes)
            this[MealRecipes.index] = recipeId.index
        }

        mealId
    }

    suspend fun readAll(): List<MealDto> {
        return dbQuery {
            MealEntity.all().toList().map {
                MealDto(
                    it.id.value,
                    it.name,
                    MealTypeDto(it.type.id.value, it.type.name, it.type.index),
                    it.recipes.map { recipe ->
                        RecipeShortDto(
                            recipe.id.value,
                            recipe.name
                        )
                    }
                )
            }
        }
    }

    suspend fun read(id: Long): MealDto? {
        return dbQuery {
            Meals.selectAll().where { Meals.id eq id }
                .map { MealEntity.wrapRow(it) }
                .map {
                    MealDto(
                        it.id.value,
                        it.name,
                        MealTypeDto(it.type.id.value, it.type.name, it.type.index),
                        it.recipes.map { recipe ->
                            RecipeShortDto(
                                recipe.id.value,
                                recipe.name
                            )
                        }
                    )
                }
                .singleOrNull()
        }
    }

    suspend fun update(id: Long, meal: CreateMealDto) {
        dbQuery {
            MealEntity.findByIdAndUpdate(id) {
                it.name = meal.name ?: ""
                it.type = MealTypeEntity.findById(meal.mealTypeId) ?: MealTypeEntity[1] // default to Breakfast
                it.recipes = SizedCollection(
                    meal.recipeIds.mapNotNull { recipeId ->
                        Recipes.selectAll().where { Recipes.id eq recipeId }
                            .map { row -> RecipeEntity.wrapRow(row) }
                            .singleOrNull()
                    }
                )
            }
        }
    }

    suspend fun addRecipe(id: Long, recipeId: Long) {
        dbQuery {
            MealRecipes.insertIgnore {
                it[meal] = EntityID(id, Meals)
                it[recipe] = EntityID(recipeId, Recipes)
            }
        }
    }

    suspend fun removeRecipe(id: Long, recipeId: Long) {
        dbQuery {
            MealRecipes.deleteWhere {
                (meal eq id) and (recipe eq recipeId)
            }
        }
    }

    suspend fun delete(id: Long) {
        dbQuery {
            Meals.deleteWhere { Meals.id.eq(id) }
        }
    }
}