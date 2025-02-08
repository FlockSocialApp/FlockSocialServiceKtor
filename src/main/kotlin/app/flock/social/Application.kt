package app.flock.social

import app.flock.social.data.dao.DatabaseFactory
import app.flock.social.plugins.*
import io.ktor.server.application.*

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused")
fun Application.module() {
    DatabaseFactory.init()
    configureRequestValidation()
    configureAutoHeadResponse()
    configureCORS()
    configureStatusPage()
    configureSecurity()
    configureHTTP()
    configureSerialization()
    configureRouting()
    configureSwagger()
}


