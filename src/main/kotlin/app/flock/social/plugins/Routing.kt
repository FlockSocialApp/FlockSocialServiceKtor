package app.flock.social.plugins

import app.flock.social.route.eventsRoute
import app.flock.social.route.mailingListRoutes
import app.flock.social.route.paymentRoutes
import app.flock.social.route.supabaseAuthRoutes
import io.ktor.server.application.Application
import io.ktor.server.routing.routing

fun Application.configureRouting() {
    routing {
        paymentRoutes()
        mailingListRoutes()
        supabaseAuthRoutes()
        eventsRoute()
    }
}
