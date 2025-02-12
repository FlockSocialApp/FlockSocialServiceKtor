package app.flock.social.data.dao

import app.flock.social.util.EnvConfig
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
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
        val database = Database.connect(
            url = EnvConfig.databaseUrl,
            driver = "org.postgresql.Driver",
            user = "postgres",
            password = EnvConfig.databasePw
        )
        transaction(database) {
//            SchemaUtils.create(
//                BookmarksTable,
//                CommunityTable,
//                EventsTable,
//                FollowsTable,
//                RsvpsTable,
//                UsersTable
//            )
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}