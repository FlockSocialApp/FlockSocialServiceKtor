package app.flock.social.route

import app.flock.social.data.table.EventDTO
import app.flock.social.supabase.supabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get

fun Routing.eventsRoute() {
    get("/events/{id}") {
        val eventId = call.parameters["id"] ?: call.respond(HttpStatusCode.BadRequest)
        try {
            val entry = supabaseClient
                .from("events")
                .select(columns = Columns.list("id", "display_name", "description")) {
                    filter { eq("id", eventId) }
                }
                .decodeSingleOrNull<EventDTO>() ?: throw Throwable()

            call.respond(
                HttpStatusCode.OK,
                entry
            )
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest)
        }
    }
}