package app.flock.social.data.table

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.time.LocalDateTime

@Serializable
data class BookmarkDTO(
    val id: String,
    val eventId: String,
    val userId: String
)

object BookmarksTable : Table("user_event_bookmarks") {
    val id = uuid("id")
    val eventId = uuid("event_id").references(EventsTable.id, onDelete = ReferenceOption.CASCADE)
    val userId = uuid("user_id").references(UsersTable.id, onDelete = ReferenceOption.CASCADE)
    val createdAt = datetime("created_at").clientDefault { LocalDateTime.now() }
    val updatedAt = datetime("updated_at").clientDefault { LocalDateTime.now() }

    override val primaryKey: PrimaryKey = PrimaryKey(id)
}

class BookmarkDao {
    fun getAllBookmarks(): List<BookmarkDTO> {
        return transaction {
            BookmarksTable
                .selectAll()
                .map { bookmark ->
                    BookmarkDTO(
                        id = bookmark[BookmarksTable.id].toString(),
                        eventId = bookmark[BookmarksTable.eventId].toString(),
                        userId = bookmark[BookmarksTable.userId].toString()
                    )
                }
        }
    }

    fun getBookmarkById(id: String): BookmarkDTO? {
        return transaction {
            BookmarksTable
                .select { BookmarksTable.id eq java.util.UUID.fromString(id) }
                .map { bookmark ->
                    BookmarkDTO(
                        id = bookmark[BookmarksTable.id].toString(),
                        eventId = bookmark[BookmarksTable.eventId].toString(),
                        userId = bookmark[BookmarksTable.userId].toString()
                    )
                }
                .firstOrNull()
        }
    }

    fun createBookmark(bookmark: BookmarkDTO) {
        transaction {
            BookmarksTable.insert {
                it[id] = java.util.UUID.fromString(bookmark.id)
                it[eventId] = java.util.UUID.fromString(bookmark.eventId)
                it[userId] = java.util.UUID.fromString(bookmark.userId)
                it[createdAt] = LocalDateTime.now()
                it[updatedAt] = LocalDateTime.now()
            }
        }
    }

    fun updateBookmark(bookmark: BookmarkDTO) {
        transaction {
            BookmarksTable.update({ BookmarksTable.id eq java.util.UUID.fromString(bookmark.id) }) {
                it[eventId] = java.util.UUID.fromString(bookmark.eventId)
                it[userId] = java.util.UUID.fromString(bookmark.userId)
                it[updatedAt] = LocalDateTime.now()
            }
        }
    }

    fun deleteBookmark(id: String) {
        transaction {
            BookmarksTable.deleteWhere { BookmarksTable.id eq java.util.UUID.fromString(id) }
        }
    }
}