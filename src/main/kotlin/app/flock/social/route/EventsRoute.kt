package app.flock.social.route

import app.flock.social.data.dao.EventDao
import app.flock.social.data.table.EventDTO
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put

fun Routing.eventsRoute(
    eventDao: EventDao,
) {
    authenticate("auth-jwt") {
        // Get event by id
        get("/events/{id}") {
            val eventId = call.parameters["id"] ?: call.respond(HttpStatusCode.BadRequest)
            try {
                val entry = eventDao.getEventWithAttendees(
                    eventId.toString()
                )
                call.respond(
                    entry
                )
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, e.localizedMessage)
            }
        }

        // Get all events
        get("/events") {
            try {
                val entries = eventDao.getAllEvents()
                call.respond(entries)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, e.localizedMessage)
            }
        }

        // Create event
        post("/events") {
            try {
                val event = call.receive<EventDTO>()
                eventDao.createEvent(event)
                call.respond(HttpStatusCode.Created)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest)
            }
        }

        // Update event
        put("/events/{id}") {
            val eventId = call.parameters["id"] ?: call.respond(HttpStatusCode.BadRequest)
            try {
                val event = call.receive<EventDTO>()
                eventDao.updateEvent(
                    event
                )
                call.respond(HttpStatusCode.OK)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest)
            }
        }

        // Delete event
        delete("/events/{id}") {
            val eventId = call.parameters["id"] ?: call.respond(HttpStatusCode.BadRequest)
            try {
                eventDao.deleteEvent(eventId.toString())
                call.respond(HttpStatusCode.OK)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest)
            }
        }
    }
}