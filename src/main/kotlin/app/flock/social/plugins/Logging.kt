package app.flock.social.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.createApplicationPlugin
import io.ktor.server.application.install
import io.ktor.server.request.uri
import io.ktor.util.AttributeKey
import org.slf4j.LoggerFactory

fun Application.configureLogging() {
    install(RequestTimingPlugin)
}

val logger = LoggerFactory.getLogger("ApplicationLogger")

val RequestTimingPlugin = createApplicationPlugin("RequestTiming") {
    onCall { call ->
        val startTime = System.currentTimeMillis()
        call.attributes.put(AttributeKey("startTime"), startTime)
    }

    onCallRespond { call, _ ->
        val startTime = call.attributes.getOrNull(AttributeKey<Long>("startTime")) ?: return@onCallRespond
        val duration = System.currentTimeMillis() - startTime
        logger.info("Request to ${call.request.uri} took ${duration}ms")
    }
}