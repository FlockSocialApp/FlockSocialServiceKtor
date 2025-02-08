package app.flock.social.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.autohead.*

fun Application.configureAutoHeadResponse(){
    install(AutoHeadResponse)
}