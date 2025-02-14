package app.flock.social.route

import app.flock.social.data.table.CommunityDTO
import app.flock.social.data.dao.CommunityDao
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put

fun Routing.communityRoute(
    communityDao: CommunityDao,
) {
    // Get community by id
    get("/communities/{id}") {
        val communityId = call.parameters["id"] ?: call.respond(HttpStatusCode.BadRequest)
        try {
            val entry = communityDao.getCommunityById(
                communityId.toString()
            ) ?: throw Throwable("Community not found")

            call.respond(
                HttpStatusCode.OK,
                entry
            )
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest)
        }
    }

    // Get all communities
    get("/communities") {
        try {
            val entries = communityDao.getAllCommunities()
            call.respond(HttpStatusCode.OK, entries)
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest)
        }
    }

    // Create community
    post("/communities") {
        try {
            val community = call.receive<CommunityDTO>()
            communityDao.createCommunity(community)
            call.respond(HttpStatusCode.Created)
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest)
        }
    }

    // Update community
    put("/communities") {
        try {
            val community = call.receive<CommunityDTO>()
            communityDao.updateCommunity(community)
            call.respond(HttpStatusCode.OK)
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest)
        }
    }

    // Delete community
    delete("/communities/{id}") {
        val communityId = call.parameters["id"] ?: call.respond(HttpStatusCode.BadRequest)
        try {
            communityDao.deleteCommunity(communityId.toString())
            call.respond(HttpStatusCode.OK)
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest)
        }
    }
}
