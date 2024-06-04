package filip.vinkovic.db.dao

import filip.vinkovic.db.table.*
import filip.vinkovic.model.*
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

class MealPlanDao(
    private val mealDao: MealDao
) {

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    suspend fun create(meal: CreateMealPlanDto): Long = dbQuery {
        MealPlanEntity.new {
            name = meal.name
        }.id.value
    }

    suspend fun readAll(): List<MealPlanDto> {
        return dbQuery {
            MealPlans.select(MealPlans.id)
                .mapNotNull { read(it[MealPlans.id].value) }
        }
    }

    suspend fun read(id: Long): MealPlanDto? {
        return dbQuery {
            MealPlans.selectAll().where { MealPlans.id eq id }
                .map { mealPlanRow ->
                    val mealPlan = MealPlanEntity.wrapRow(mealPlanRow)
                    val dailyPlans = mutableMapOf<Int, List<MealEntity>>()

                    MealPlanMeals.selectAll().where { MealPlanMeals.mealPlan eq mealPlan.id }
                        .map {
                            val day = it[MealPlanMeals.day]
                            val mealEntity = MealEntity.wrap(it[MealPlanMeals.meal], it)
                            dailyPlans[day] = dailyPlans.getOrDefault(day, emptyList()) + mealEntity
                        }

                    MealPlanDto(
                        mealPlan.id.value,
                        mealPlan.name,
                        dailyPlans.map { (day, meals) ->
                            DailyPlanDto(
                                day,
                                meals.map { meal ->
                                    MealDto(
                                        meal.id.value,
                                        meal.name,
                                        MealTypeDto(meal.type.id.value, meal.type.name, meal.type.index),
                                        meal.recipes.map { recipe ->
                                            RecipeShortDto(
                                                recipe.id.value,
                                                recipe.name
                                            )
                                        }
                                    )
                                }
                            )
                        }
                    )
                }.singleOrNull()
        }
    }

    suspend fun addRecipe(mealPlanId: Long, day: Int, mealTypeId: Long, recipeId: Long) {
        return dbQuery {
            var mealId: Long? = MealPlanMeals.selectAll().where {
                (MealPlanMeals.mealPlan eq mealPlanId) and
                        (MealPlanMeals.day eq day) and
                        (MealPlanMeals.mealType eq mealTypeId)
            }.map {
                MealEntity.wrap(it[MealPlanMeals.meal], it)
            }.singleOrNull()?.id?.value

            if (mealId == null) {
                mealId = mealDao.create(
                    CreateMealDto(
                        mealTypeId = mealTypeId,
                        recipeIds = listOf(recipeId),
                        name = null
                    )
                )
            } else {
                mealDao.addRecipe(mealId, recipeId)
            }
            addMeal(mealPlanId, day, mealId, mealTypeId)
        }
    }

    suspend fun removeRecipe(mealId: Long, recipeId: Long) {
        dbQuery {
            mealDao.removeRecipe(mealId, recipeId)
        }
    }

    suspend fun addMeal(mealPlanId: Long, day: Int, mealId: Long, mealTypeId: Long) {
        dbQuery {
            MealPlanMeals.insertIgnore {
                it[mealPlan] = EntityID(mealPlanId, MealPlans)
                it[meal] = EntityID(mealId, Meals)
                it[MealPlanMeals.day] = day
                it[mealType] = EntityID(mealTypeId, MealTypes)
            }
        }
    }

    suspend fun removeMeal(mealPlanId: Long, day: Int, mealId: Long) {
        dbQuery {
            MealPlanMeals.deleteWhere {
                (mealPlan eq mealPlanId) and
                        (meal eq mealId) and
                        (MealPlanMeals.day eq day)
            }
        }
    }

    suspend fun delete(id: Long) {
        dbQuery {
            MealPlans.deleteWhere { MealPlans.id.eq(id) }
        }
    }
}