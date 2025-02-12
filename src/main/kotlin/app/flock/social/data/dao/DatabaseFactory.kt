package app.flock.social.data.dao

import app.flock.social.data.table.UsersTable
import app.flock.social.util.EnvConfig
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * host:
 * db.lgtphxjfpjstzogfifkw.supabase.co
 *
 * port:
 * 5432
 *
 * database:
 * postgres
 *
 * user:
 * postgres
 *
 *
 */
object DatabaseFactory {
    fun init() {
        try {
            println("Connecting to database...")
            val database = Database.connect(
                url = EnvConfig.databaseUrl,
                driver = "org.postgresql.Driver",
                user = EnvConfig.databaseUser,
                password = EnvConfig.databasePw
            )

            transaction(database) {
                // Add SQL logging
                addLogger(StdOutSqlLogger)
                
                // Test basic connection and permissions
                println("Testing connection...")
                
                // Try to create just the users table
                println("Attempting to create UsersTable...")
                SchemaUtils.create(UsersTable)
                println("UsersTable created successfully")
            }
        } catch (e: Exception) {
            println("Error during database operations:")
            println("Message: ${e.message}")
            println("Cause: ${e.cause?.message}")
            e.printStackTrace()
            throw e
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}