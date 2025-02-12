package app.flock.social.route

import app.flock.social.data.table.RsvpDTO
import app.flock.social.data.table.RsvpDao
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put

fun Routing.rsvpRoute(
    rsvpDao: RsvpDao,
) {
    // Get RSVP by id
    get("/rsvps/{id}") {
        val rsvpId = call.parameters["id"] ?: call.respond(HttpStatusCode.BadRequest)
        try {
            val entry = rsvpDao.getRsvpById(rsvpId.toString()) ?: throw Throwable("Rsvp not found for ID")
            call.respond(HttpStatusCode.OK, entry)
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest)
        }
    }

    // Get all RSVPs
    get("/rsvps") {
        try {
            val entries = rsvpDao.getAllRsvps()
            call.respond(HttpStatusCode.OK, entries)
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest)
        }
    }
    // Create RSVP
    post("/events/{id}/rsvp") {
        try {
            val rsvp = call.receive<RsvpDTO>()
            rsvpDao.createRsvp(
                rsvp
            )
            call.respond(HttpStatusCode.Created)
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest)
        }
    }

    // Update RSVP
    put("/events/{eventId}/rsvp") {
        try {
            val rsvp = call.receive<RsvpDTO>()
            rsvpDao.updateRsvp(rsvp)
            call.respond(HttpStatusCode.OK)
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest)
        }
    }

    // Delete RSVP
    delete("/events/{eventId}/rsvp") {
        val eventId = call.parameters["eventId"] ?: call.respond(HttpStatusCode.BadRequest)
        try {
            rsvpDao.deleteRsvp(eventId.toString())
            call.respond(HttpStatusCode.OK)
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest)
        }
    }
}
