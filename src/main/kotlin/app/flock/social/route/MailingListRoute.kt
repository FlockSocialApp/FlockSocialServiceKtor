package app.flock.social.route

import app.flock.social.data.dao.MailingListDao
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MailingListSignUpRequest(
    @SerialName("email")
    val email: String,
)

fun Routing.mailingListRoutes(
    mailingListDao: MailingListDao
) {
    route("/mailing") {
        post("/sign-up") {
            try {
                val emailRequest = call.receive<MailingListSignUpRequest>()
                call.respond(
                    HttpStatusCode.OK,
                    mailingListDao.insert(emailRequest.email)
                )
            } catch (e: Exception) {
                print(e.message)
                call.respond(HttpStatusCode.InternalServerError)
            }
        }
    }
}