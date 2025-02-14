package app.flock.social.data.table

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object MailingListTable : Table("mailing_list") {
    val id = uuid("id")
    val createdAt = datetime("created_at").clientDefault { LocalDateTime.now() }
    val email = text("email")

    override val primaryKey: PrimaryKey?
        get() = PrimaryKey(id)
}