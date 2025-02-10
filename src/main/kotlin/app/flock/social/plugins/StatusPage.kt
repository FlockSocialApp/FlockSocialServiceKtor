package app.flock.social.plugins

import app.flock.social.data.ErrorMessage
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.requestvalidation.RequestValidationException
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond

fun Application.configureStatusPage(){
    install(StatusPages) {

        status(HttpStatusCode.Unauthorized){ call, status ->
            call.respond(status, ErrorMessage(message ="Unauthorized Access" ))
        }
        exception<RequestValidationException> { call, cause ->
            call.respond(HttpStatusCode.BadRequest,ErrorMessage(message =cause.reasons.joinToString() ) )
        }
        exception<Throwable> { call, cause ->
            call.respond(status = HttpStatusCode.InternalServerError,ErrorMessage(message ="500: ${cause.localizedMessage} $cause" ) )
        }
    }
}