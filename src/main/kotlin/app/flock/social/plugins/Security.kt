package app.flock.social.plugins

import app.flock.social.util.EnvConfig
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt

fun Application.configureAuthentication() {
    install(Authentication) {
        jwt("auth-jwt") {
            realm = "ktor"
            verifier(
                JWT.require(Algorithm.HMAC256(EnvConfig.jwtSecret))
                    .withAudience("authenticated")
                    .withIssuer(EnvConfig.jwtIssuer)
                    .build()
            )
            validate { credential ->
                if (credential.payload.getClaim("sub").asString() != null) {
                    // needs a userId
                    JWTPrincipal(credential.payload)
                } else {
                    null // Invalid token
                }
            }
        }
    }
}
