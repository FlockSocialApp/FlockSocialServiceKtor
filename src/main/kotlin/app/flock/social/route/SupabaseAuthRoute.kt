package app.flock.social.route

import app.flock.social.supabase.supabaseAuth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.ktor.http.HttpStatusCode
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
data class SignUpEmailPasswordRequest(
    val email: String,
    val password: String
)

@Serializable
data class AuthResponse(
    val accessToken: String,
    val refreshToken: String
)

@Serializable
data class RefreshSessionRequest(
    val refreshToken: String
)

fun Routing.supabaseAuthRoutes() {
    route("/auth") {
        post("/login") {
            val request = call.receive<LoginEmailPasswordRequest>()

            try {
                // login
                supabaseAuth.signInWith(Email) {
                    email = request.email
                    password = request.password
                }

                // Check for a curr session now
                val session = supabaseAuth.currentSessionOrNull()

                if (session == null) {
                    call.respond(
                        HttpStatusCode.Unauthorized,
                    )
                } else {
                    call.respond(
                        AuthResponse(
                            accessToken = session.accessToken,
                            refreshToken = session.refreshToken
                        )
                    )
                }
            } catch (_: Throwable) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    "Login: An error has occured"
                )
            }
        }
        post("sign-up") {
            val request = call.receive<SignUpEmailPasswordRequest>()

            try {
                // sign up
                supabaseAuth.signUpWith(Email) {
                    email = request.email
                    password = request.password
                }

                // Check for a curr session now
                val session = supabaseAuth.currentSessionOrNull()

                if (session == null) {
                    call.respond(
                        HttpStatusCode.Unauthorized,
                    )
                } else {
                    call.respond(
                        AuthResponse(
                            accessToken = session.accessToken,
                            refreshToken = session.refreshToken
                        )
                    )
                }
            } catch (_: Throwable) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    "SignUp: An error has occurred"
                )
            }
        }
        post("/refresh") {
            val request = call.receive<RefreshSessionRequest>()

            try {
                val session = supabaseAuth.refreshSession(
                    request.refreshToken
                )

                call.respond(
                    AuthResponse(
                        accessToken = session.accessToken,
                        refreshToken = session.refreshToken
                    )
                )
            } catch (_: Throwable) {
                call.respond(HttpStatusCode.Unauthorized)
            }
        }
    }
}