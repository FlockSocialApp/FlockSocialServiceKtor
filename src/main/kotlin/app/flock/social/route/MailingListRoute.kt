package app.flock.social.route

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
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
    supabaseClient: SupabaseClient = app.flock.social.supabase.supabaseClient
) {
    route("/mailing") {
        post("/sign-up") {
            try {
                val email = call.receive<MailingListSignUpRequest>()
                call.respond(
                    HttpStatusCode.OK,
                    supabaseClient.from("mailing_list").insert(email)
                )
            } catch (e: Exception) {
                print(e.message)
                call.respond(HttpStatusCode.InternalServerError)
            }
        }
    }
}