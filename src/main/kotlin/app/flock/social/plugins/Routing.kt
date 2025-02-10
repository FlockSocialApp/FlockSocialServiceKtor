package app.flock.social.plugins

import app.flock.social.route.*
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        authRoutes()
        userRoute()
        blogRoutes()
        paymentRoutes()
        mailingListRoutes()
    }
}
