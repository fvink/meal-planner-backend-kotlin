package filip.vinkovic.service

import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database

fun Application.initializeServices() {
    connectToPostgres()
    initializeIngredientService()
    initializeRecipeService()
    initializeMealService()
    initializeMealPlanService()
}

fun connectToPostgres(): Database {
    Class.forName("org.postgresql.Driver")
    val url = System.getenv("DATABASE_URL")
    val user = System.getenv("DATABASE_USER")
    val password = System.getenv("DATABASE_PASSWORD")

    return Database.connect(url, driver = "org.postgresql.Driver", user = user, password = password)
}
