package filip.vinkovic.db.dao

import filip.vinkovic.db.table.MealTypeEntity
import filip.vinkovic.db.table.MealTypes
import filip.vinkovic.model.CreateMealTypeDto
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class MealTypeDao {

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    suspend fun create(mealType: CreateMealTypeDto): Long = dbQuery {
        MealTypeEntity.new {
            name = mealType.name
        }.id.value
    }

    suspend fun readAll(): List<MealTypeEntity> {
        return dbQuery {
            MealTypeEntity.all().toList()
        }
    }

    suspend fun read(id: Long): MealTypeEntity? {
        return dbQuery {
            MealTypes.selectAll().where { MealTypes.id eq id }
                .map { MealTypeEntity.wrapRow(it) }
                .singleOrNull()
        }
    }

    suspend fun update(id: Long, mealType: CreateMealTypeDto) {
        dbQuery {
            MealTypeEntity.findByIdAndUpdate(id) {
                it.name = mealType.name
            }
        }
    }

    suspend fun delete(id: Long) {
        dbQuery {
            MealTypes.deleteWhere { MealTypes.id.eq(id) }
        }
    }
}