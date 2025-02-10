package app.flock.social.route

import app.flock.social.data.ErrorMessage
import app.flock.social.data.MyToken
import app.flock.social.data.dao.user.userDao
import app.flock.social.data.table.token.Tokens
import app.flock.social.data.table.user.Users
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.github.smiley4.ktorswaggerui.dsl.routing.get
import io.github.smiley4.ktorswaggerui.dsl.routing.post
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Routing
import io.ktor.server.routing.route
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

fun Routing.userRoute() {
    val accessTokenExpiryTime=60000*30
    get("/name") {
        call.respondText("Hello World!")
    }
    get(
        "/allUser",
        {
            tags = listOf("default")
            response {
                HttpStatusCode.OK to {
                    description = "The operation was successful"
                    body<Users>()

                }
                HttpStatusCode.InternalServerError to {
                    description = "Internal Server Error"
                    body<ErrorMessage> {
                    }
                }
            }
        },
    ) {
        call.respond(Users(users = userDao.getAllUser()))
    }
    get("/allTokens", {
        tags = listOf("default")
        response {
            HttpStatusCode.OK to {
                description = "The operation was successful"
                body<Tokens> {
                }

            }
            HttpStatusCode.InternalServerError to {
                description = "Internal Server Error"
                body<ErrorMessage> {
                }
            }
        }
    }) {
        call.respond(
            Tokens(tokens = app.flock.social.data.dao.token.tokenDao.getAllToken())
        )
    }

    authenticate("authJWT") {
        route("/user") {
            get(
                "/hello",
                {
                    tags = listOf("User")

                },
            ) {
                val principal = call.principal<JWTPrincipal>()
                val username = principal!!.payload.getClaim("username").asString()
                val expiresAt = principal.expiresAt?.time

                val formatter = SimpleDateFormat("dd/MM/yyyy hh:mm:ss").apply {
                    timeZone = (TimeZone.getTimeZone("Asia/Kolkata"))
                }
                val expiredTime = formatter.format(expiresAt?.let { it1 -> Date(it1) })
                val json = mapOf("expireAtDate" to expiredTime, "username" to username)

                call.respond(message = json, status = HttpStatusCode.OK)

            }

            get(
                "/ok",
                {
                    tags = listOf("User")
                },
            ) {
                val principal = call.principal<JWTPrincipal>()
                val username = principal!!.payload.getClaim("username").asString()
                call.respondText { "Hey$username Authenticated.... :)" }
            }
        }
    }




    authenticate("auth-refresh-jwt") {
        val audience = this@userRoute.environment?.config?.property("jwt.audience")?.getString()
        val issuer = this@userRoute.environment?.config?.property("jwt.domain")?.getString()
        val secret = this@userRoute.environment?.config?.property("jwt.secret")?.getString()
        route("/user") {
            post(
                "/refreshAccessToken",
                {
                    tags = listOf("User")
                    response {
                        HttpStatusCode.OK to {
                            description = "The operation was successful"
                            body<MyToken>()

                        }
                        HttpStatusCode.BadRequest to {
                            description = "Bad Request"
                            body<ErrorMessage> {
                            }
                        }
                        HttpStatusCode.InternalServerError to {
                            description = "Internal Server Error"
                            body<ErrorMessage> {
                            }
                        }
                    }

                },
            ) {
                val principal = call.principal<JWTPrincipal>()
                val username = principal!!.payload.getClaim("username").asString()
                val userid = principal.payload.getClaim("userid").asInt()
                principal.payload.getClaim("tokenType").asString()
                val expiresAt = principal.expiresAt?.time

                val formatter = SimpleDateFormat("dd/MM/yyyy hh:mm:ss").apply {
                    timeZone = (TimeZone.getTimeZone("Asia/Kolkata"))
                }

                formatter.format(expiresAt?.let { it1 -> Date(it1) })

                if (principal.expiresAt?.after(Date()) == true) {
                    val accessToken = JWT.create()
                        .withAudience(audience)
                        .withIssuer(issuer)
                        .withClaim("userid", userid)
                        .withClaim("username", username)
                        .withClaim("tokenType", "accessToken")
                        .withExpiresAt(Date(System.currentTimeMillis() + accessTokenExpiryTime))
                        .sign(Algorithm.HMAC256(secret))
                    val isReplaced = app.flock.social.data.dao.token.tokenDao.replaceAccessToken(userId = userid, accessToken)
                    if (isReplaced) {
                        val token = app.flock.social.data.dao.token.tokenDao.getTokens(userid)
                        if (token != null) {
                            token.accessToken?.let { it1 -> MyToken(accessToken= it1, refreshToken = token.refreshToken) }
                                ?.let { it2 -> call.respond(it2) }
                        } else {
                            call.respond(
                                status = HttpStatusCode.BadRequest,
                                ErrorMessage(message = "Failed to get token")
                            )
                        }
                    } else {
                        call.respond(status = HttpStatusCode.BadRequest, ErrorMessage(message = "Failed replace token"))
                    }
                } else {
                    call.respond(
                        status = HttpStatusCode.BadRequest,
                        ErrorMessage(message = "Failed to get token")
                    )
                }
            }
        }
    }
}