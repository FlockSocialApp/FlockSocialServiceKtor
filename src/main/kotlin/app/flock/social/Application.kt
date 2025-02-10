package app.flock.social

import app.flock.social.data.dao.DatabaseFactory
import app.flock.social.plugins.configureAutoHeadResponse
import app.flock.social.plugins.configureCORS
import app.flock.social.plugins.configureHTTP
import app.flock.social.plugins.configureRequestValidation
import app.flock.social.plugins.configureRouting
import app.flock.social.plugins.configureSecurity
import app.flock.social.plugins.configureSerialization
import app.flock.social.plugins.configureStatusPage
import app.flock.social.plugins.configureSwagger
import app.flock.social.stripe.StripeConfig
import app.flock.social.util.EnvConfig
import io.ktor.server.application.Application

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
    StripeConfig.initialize(EnvConfig.stripeSK)
}


