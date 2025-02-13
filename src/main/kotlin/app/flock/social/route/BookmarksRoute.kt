package app.flock.social.route

import app.flock.social.data.table.BookmarkDTO
import app.flock.social.data.table.BookmarkDao
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put

fun Routing.bookmarksRoute(
    bookmarksDao: BookmarkDao,
) {
    // Get bookmark by id
    get("/bookmarks/{id}") {
        val bookmarkId = call.parameters["id"] ?: call.respond(HttpStatusCode.BadRequest)
        try {
            val entry = bookmarksDao.getBookmarkById(
                bookmarkId.toString()
            ) ?: throw Throwable("Bookmark not found")

            call.respond(
                HttpStatusCode.OK,
                entry
            )
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest)
        }
    }

    // Get all bookmarks
    get("/bookmarks") {
        try {
            val entries = bookmarksDao.getAllBookmarks()
            call.respond(HttpStatusCode.OK, entries)
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest)
        }
    }

    // Create bookmark
    post("/bookmarks") {
        try {
            val bookmark = call.receive<BookmarkDTO>()
            bookmarksDao.createBookmark(bookmark)
            call.respond(HttpStatusCode.Created)
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest)
        }
    }

    // Update bookmark
    put("/bookmarks") {
        try {
            val bookmark = call.receive<BookmarkDTO>()
            bookmarksDao.updateBookmark(bookmark)
            call.respond(HttpStatusCode.OK)
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest)
        }
    }

    // Delete bookmark
    delete("/bookmarks/{id}") {
        val bookmarkId = call.parameters["id"] ?: call.respond(HttpStatusCode.BadRequest)
        try {
            bookmarksDao.deleteBookmark(bookmarkId.toString())
            call.respond(HttpStatusCode.OK)
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest)
        }
    }
}