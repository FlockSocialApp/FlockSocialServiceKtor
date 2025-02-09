package app.flock.social.plugins

import app.flock.social.route.authRoutes
import app.flock.social.route.blogRoutes
import app.flock.social.route.paymentRoutes
import app.flock.social.route.userRoute
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        authRoutes()
        userRoute()
        blogRoutes()
        paymentRoutes()
    }
}
