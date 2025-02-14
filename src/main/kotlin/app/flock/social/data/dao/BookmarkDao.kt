package app.flock.social.data.dao

import app.flock.social.data.table.BookmarkDTO
import app.flock.social.data.table.BookmarksTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.time.LocalDateTime
import java.util.UUID

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
                .select { BookmarksTable.id eq UUID.fromString(id) }
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
                it[id] = UUID.fromString(bookmark.id)
                it[eventId] = UUID.fromString(bookmark.eventId)
                it[userId] = UUID.fromString(bookmark.userId)
                it[createdAt] = LocalDateTime.now()
                it[updatedAt] = LocalDateTime.now()
            }
        }
    }

    fun updateBookmark(bookmark: BookmarkDTO) {
        transaction {
            BookmarksTable.update({ BookmarksTable.id eq UUID.fromString(bookmark.id) }) {
                it[eventId] = UUID.fromString(bookmark.eventId)
                it[userId] = UUID.fromString(bookmark.userId)
                it[updatedAt] = LocalDateTime.now()
            }
        }
    }

    fun deleteBookmark(id: String) {
        transaction {
            BookmarksTable.deleteWhere { BookmarksTable.id eq UUID.fromString(id) }
        }
    }
}