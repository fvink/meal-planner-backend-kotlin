package filip.vinkovic.db.dao

import filip.vinkovic.db.table.IngredientEntity
import filip.vinkovic.db.table.Ingredients
import filip.vinkovic.model.CreateIngredientDto
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

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
            if (ingredient.amount != null && ingredient.unit != null) {
                row[weight_g] = ingredient.amount
                // TODO: Convert DTO unit to grams, add support for volume_ml
            }
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

    suspend fun read(name: String): List<IngredientEntity> {
        return dbQuery {
            Ingredients.selectAll()
                .where { Ingredients.name.lowerCase() like "%${name.lowercase()}%" }
                .orderBy(
                    Case()
                        .When(Ingredients.name.lowerCase() eq name.lowercase(), intLiteral(1))
                        .When(Ingredients.name.lowerCase() like "${name.lowercase()}%", intLiteral(2))
                        .Else(intLiteral(3)) to SortOrder.ASC,
                    Ingredients.name to SortOrder.ASC
                )
                .limit(30)
                .map { IngredientEntity.wrapRow(it) }
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
                if (ingredient.amount != null && ingredient.unit != null) {
                    row[weight_g] = ingredient.amount
                    // TODO: Convert DTO unit to grams, add support for volume_ml
                }
            }
        }
    }

    suspend fun delete(id: Long) {
        dbQuery {
            Ingredients.deleteWhere { Ingredients.id.eq(id) }
        }
    }
}
