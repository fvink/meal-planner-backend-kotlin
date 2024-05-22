package filip.vinkovic.service

import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database


fun Application.initializeServices() {
    connectToPostgres()
    initializeIngredientService()
    initializeRecipeService()
    initializeMealService()
    initializeMealPlanService()
    initializeMealTypeService()
}

fun connectToPostgres(): Database {
    Class.forName("org.postgresql.Driver")
    val url = System.getenv("DATABASE_URL")
    val user = System.getenv("DATABASE_USER")
    val password = System.getenv("DATABASE_PASSWORD")

    val dataSource = HikariDataSource()
    dataSource.jdbcUrl = url
    dataSource.username = user
    dataSource.password = password

    return Database.connect(dataSource)
}
