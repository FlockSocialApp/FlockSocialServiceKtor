package app.flock.social.data.dao

import app.flock.social.data.table.MailingListTable
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

class MailingListDao {
    fun insert(emailStr: String) {
        transaction {
            MailingListTable.insert {
                it[id] = UUID.randomUUID()
                it[email] = emailStr
                it[createdAt] = java.time.LocalDateTime.now()
            }
        }
    }
}