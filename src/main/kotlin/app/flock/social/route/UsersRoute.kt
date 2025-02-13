package app.flock.social.route

import app.flock.social.data.table.UsersDao
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get

fun Routing.usersRoute(
    usersDao: UsersDao
) {
    get("/users") {
        try {
            val users = usersDao.getAllUsers()
            call.respond(
                HttpStatusCode.OK,
                users
            )
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest)
        }
    }

    get("/users/{id}") {
        val userId = call.parameters["id"] ?: call.respond(HttpStatusCode.BadRequest)
        try {
            val entry = usersDao.getUserById(
                userId.toString()
            ) ?: throw Throwable("Bookmark not found")

            call.respond(
                HttpStatusCode.OK,
                entry
            )
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest)
        }
    }
}