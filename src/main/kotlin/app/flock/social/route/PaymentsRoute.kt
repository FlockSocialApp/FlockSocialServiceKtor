package app.flock.social.route

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Routing.paymentsRoute() {
    get("/payments") {
        call.respond("hello")
    }
}