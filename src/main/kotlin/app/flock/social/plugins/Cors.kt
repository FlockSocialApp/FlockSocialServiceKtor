package app.flock.social.plugins

import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.cors.routing.CORS


fun Application.configureCORS() {
    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Patch)
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)
        allowCredentials = true
        allowHeaders { true }
        allowSameOrigin = true
        val envHost = System.getenv("RAILWAY_STATIC_URL")
        if (envHost != null) {
            val url = "https://${envHost}"
            allowHost(envHost)
            allowHost(envHost)
            allowHost("$envHost:8080")
            allowHost(envHost, subDomains = listOf("en", "de", "es"))
            allowHost(envHost, schemes = listOf("http", "https"))
            hosts.add(url)
        }

        allowHost("flocksocial.app", schemes = listOf("http", "https"))
        anyHost() // @TODO: Don't do this in production if possible. Try to limit it.
    }
}