package app.flock.social.data.dao

import app.flock.social.data.table.BookmarksTable
import app.flock.social.data.table.CommunityTable
import app.flock.social.data.table.EventsTable
import app.flock.social.data.table.FollowsTable
import app.flock.social.data.table.RsvpsTable
import app.flock.social.data.table.UsersTable
import app.flock.social.util.EnvConfig
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init() {
        val database = Database.connect(
            url = EnvConfig.databaseUrl,
            driver = "org.postgresql.Driver",
            user = EnvConfig.databaseUser,
            password = EnvConfig.databasePw
        )

        transaction(database) {
            SchemaUtils.create(
                UsersTable,
                CommunityTable,
                EventsTable,
                FollowsTable,
                RsvpsTable,
                BookmarksTable
            )
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}