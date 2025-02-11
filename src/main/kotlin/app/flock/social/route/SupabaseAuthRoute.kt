package app.flock.social.route

import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import kotlinx.serialization.Serializable

@Serializable
data class LoginEmailPasswordRequest(
    val email: String,
    val password: String
)

@Serializable
data class LoginEmailPasswordResponse(
    val jwtToken: String
)

@Serializable
data class SignUpEmailPasswordRequest(
    val email: String,
    val password: String
)

@Serializable
data class SignUpEmailPasswordResponse(
    val jwtToken: String
)

fun Routing.supabaseAuthRoutes() {
    route("/auth") {
        post("/login") {
            val a = call.receive<LoginEmailPasswordRequest>()

            call.respond(
                LoginEmailPasswordResponse("")
            )
        }
    }
}