package filip.vinkovic.db.dao

import filip.vinkovic.db.table.IngredientEntity
import filip.vinkovic.db.table.Ingredients
import filip.vinkovic.model.CreateIngredientDto
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.update

class IngredientDao {

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    suspend fun create(ingredient: CreateIngredientDto): Long = dbQuery {
        Ingredients.insertAndGetId { row ->
            ingredient.name?.let { row[name] = it }
            ingredient.calories?.let { row[calories] = it }
            ingredient.protein?.let { row[protein] = it }
            ingredient.carbs?.let { row[carbs] = it }
            ingredient.fat?.let { row[fat] = it }
            ingredient.amount?.let { row[amount] = it }
            ingredient.unit?.let { row[unit] = it }
        }.value
    }

    suspend fun readAll(): List<IngredientEntity> {
        return dbQuery {
            IngredientEntity.all().toList()
        }
    }

    suspend fun read(id: Long): IngredientEntity? {
        return dbQuery {
            Ingredients.selectAll().where { Ingredients.id eq id }
                .map { IngredientEntity.wrapRow(it) }
                .singleOrNull()
        }
    }

    suspend fun update(id: Long, ingredient: CreateIngredientDto) {
        dbQuery {
            Ingredients.update({ Ingredients.id eq id }) { row ->
                ingredient.name?.let { row[name] = it }
                ingredient.calories?.let { row[calories] = it }
                ingredient.protein?.let { row[protein] = it }
                ingredient.carbs?.let { row[carbs] = it }
                ingredient.fat?.let { row[fat] = it }
                ingredient.amount?.let { row[amount] = it }
                ingredient.unit?.let { row[unit] = it }
            }
        }
    }

    suspend fun delete(id: Long) {
        dbQuery {
            Ingredients.deleteWhere { Ingredients.id.eq(id) }
        }
    }
}
