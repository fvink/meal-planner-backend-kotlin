package filip.vinkovic.db.table

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.datetime

object Users : LongIdTable() {
    val email = varchar("email", 255)
    val createdAt = datetime("created_at")
    val role = varchar("role", 50)
}

class UserEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<UserEntity>(Users)

    var email by Users.email
    var createdAt by Users.createdAt
    var role by Users.role
}