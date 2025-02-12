package app.flock.social.route

import app.flock.social.data.table.FollowDTO
import app.flock.social.data.table.FollowsDao
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put

fun Routing.followsRoute(
    followsDao: FollowsDao,
) {

    // Get follow by id
    get("/follows/{id}") {
        val followId = call.parameters["id"] ?: call.respond(HttpStatusCode.BadRequest)
        try {
            val entry = followsDao.getFollowById(
                followId.toString()
            ) ?: throw Throwable("Follow not found")

            call.respond(
                HttpStatusCode.OK,
                entry
            )
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest)
        }
    }

    // Get all follows
    get("/follows") {
        try {
            val entries = followsDao.getAllFollows()
            call.respond(HttpStatusCode.OK, entries)
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest)
        }
    }

    // Create follow
    post("/follows") {
        try {
            val follow = call.receive<FollowDTO>()
            followsDao.createFollow(follow)
            call.respond(HttpStatusCode.Created)
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest)
        }
    }

    // Update follow
    put("/follows") {
        try {
            val follow = call.receive<FollowDTO>()
            followsDao.updateFollow(follow)
            call.respond(HttpStatusCode.OK)
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest)
        }
    }

    // Delete follow
    delete("/follows/{id}") {
        val followId = call.parameters["id"] ?: call.respond(HttpStatusCode.BadRequest)
        try {
            followsDao.deleteFollow(followId.toString())
            call.respond(HttpStatusCode.OK)
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest)
        }
    }
}