package filip.vinkovic.db.dao

import filip.vinkovic.db.table.UserEntity
import filip.vinkovic.db.table.Users
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.time.LocalDateTime

class UserDao {
    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    suspend fun createUser(email: String, role: String): UserEntity? = dbQuery {
        val id = Users.insertAndGetId { row ->
            row[Users.email] = email
            row[createdAt] = LocalDateTime.now()
            row[Users.role] = role
        }.value
        read(id)
    }

    suspend fun read(id: Long): UserEntity? = dbQuery {
        Users.selectAll().where { Users.id eq id }
            .map { UserEntity.wrapRow(it) }
            .singleOrNull()
    }

    suspend fun read(email: String): UserEntity? = dbQuery {
        Users.selectAll().where { Users.email eq email }
            .map { UserEntity.wrapRow(it) }
            .singleOrNull()
    }
}