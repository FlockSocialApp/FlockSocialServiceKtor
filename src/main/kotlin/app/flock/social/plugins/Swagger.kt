package app.flock.social.plugins

import app.flock.social.data.ErrorMessage
import io.github.smiley4.ktorswaggerui.SwaggerUI
import io.github.smiley4.ktorswaggerui.data.AuthKeyLocation
import io.github.smiley4.ktorswaggerui.data.AuthScheme
import io.github.smiley4.ktorswaggerui.data.AuthType
import io.github.smiley4.ktorswaggerui.data.SwaggerUiSort
import io.github.smiley4.ktorswaggerui.data.SwaggerUiSyntaxHighlight
import io.ktor.server.application.Application
import io.ktor.server.application.host
import io.ktor.server.application.install
import io.ktor.server.application.port

fun Application.configureSwagger() {
    val engineenv = environment
    val envHost = System.getenv("RAILWAY_STATIC_URL")
    val envPort = engineenv.config.port

    install(SwaggerUI) {
        swagger {
//             authentication = "auth-jwt"
            onlineSpecValidator()

            displayOperationId = true
            showTagFilterInput = true
            sort = SwaggerUiSort.HTTP_METHOD
            syntaxHighlight = SwaggerUiSyntaxHighlight.MONOKAI
        }
        info {
            title = "Api"
            version = "v1.2"
            description = "My Api"
            termsOfService = "http://example.com/terms"
            contact {
                name = "API Support"
                url = "http://www.example.com/support"
                email = "support@example.com"
            }
            license {
                name = "Apache 2.0"
                url = "https://www.apache.org/licenses/LICENSE-2.0.html"
            }
        }
        if (envHost != null) {
            server {
                url = "https://${envHost}"
                description = "Development server"
            }
        }
        server {
            url = "https://${engineenv.config.host}:$envPort"
            description = "Development server"
        }

        security {
            securityScheme("authJWT") {
                type = AuthType.API_KEY
                scheme = AuthScheme.BEARER
                bearerFormat = "jwt"
                location = AuthKeyLocation.HEADER
            }
        }

        security {
            defaultSecuritySchemeNames = listOf("authJWT")
            defaultUnauthorizedResponse {
                description = "Unauthorized Access"
                body<ErrorMessage> {

                }
            }
        }
    }
}