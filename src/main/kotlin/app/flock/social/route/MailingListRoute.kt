package app.flock.social.route

import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import kotlinx.serialization.Serializable

@Serializable
data class MailingListSignUpRequest(
    val emailAddress: String,
)

fun Routing.mailingListRoutes() {
    route("/mailing") {
        post("/sign-up") {
            val email = call.receive<MailingListSignUpRequest>()

            // TODO here
            call.respond(
                HttpStatusCode.OK
            )
        }
    }
}