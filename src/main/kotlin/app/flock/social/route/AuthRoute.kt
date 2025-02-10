package app.flock.social.route

import app.flock.social.data.ErrorMessage
import app.flock.social.data.MyToken
import app.flock.social.data.SuccessMessage
import app.flock.social.data.UserLoginRequest
import app.flock.social.data.UserSignUpRequest
import app.flock.social.data.dao.token.TokenType
import app.flock.social.data.dao.user.userDao
import app.flock.social.data.table.token.Token
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.github.smiley4.ktorswaggerui.dsl.routing.post
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import java.util.Date

fun Routing.authRoutes() {
    val audience = this@authRoutes.environment?.config?.property("jwt.audience")?.getString()
    val issuer = this@authRoutes.environment?.config?.property("jwt.domain")?.getString()
    val secret = this@authRoutes.environment?.config?.property("jwt.secret")?.getString()
    val accessTokenExpiryTime=60000*30
    val refreshTokenExpiryTime=60000 * 60*24

    post(
        "/login",
        {
            tags = listOf("Auth")
            description = "Performs the given operation on the given values and returns the result"
            request {
                body<UserLoginRequest> {
                    description = "User with username and password"
                    required = true
                    example("First") {
                        UserLoginRequest(username = "Arjun", password = "password")
                    }
                }
            }
            response {
                HttpStatusCode.OK to {
                    description = "The operation was successful"
                    body<MyToken>()

                }
                HttpStatusCode.BadRequest to {
                    description = "Something went wrong"
                    body<ErrorMessage> {
                        example("Bad request") {
                            ErrorMessage(message = "Invalid username or password")
                        }
                    }
                }
                HttpStatusCode.InternalServerError to {
                    description = "Internal Server Error"
                    body<ErrorMessage> {
                        example("Internal Server Error") {
                            ErrorMessage(message = "Internal Server Error")
                        }
                    }
                }
            }

        },
    ) {
        val userLoginRequest = call.receive<UserLoginRequest>()
        val user = userDao.getUser(userLoginRequest)
        when {
            user != null -> {
                val isTokenAvailable = user.id?.let { it1 -> app.flock.social.data.dao.token.tokenDao.isTokenAvailable(userId = it1) }
                when {
                    isTokenAvailable != null && isTokenAvailable == true -> {
                        app.flock.social.data.dao.token.tokenDao.deleteToken(tokenType = TokenType.allToken, userId = user.id)
                        val accessToken = JWT.create()
                            .withAudience(audience)
                            .withIssuer(issuer)
                            .withClaim("userid", user.id)
                            .withClaim("username", userLoginRequest.username)
                            .withClaim("tokenType", "accessToken")
                            .withExpiresAt(Date(System.currentTimeMillis() + accessTokenExpiryTime))
                            .sign(Algorithm.HMAC256(secret))
                        val refreshToken = JWT.create()
                            .withAudience(audience)
                            .withIssuer(issuer)
                            .withClaim("userid", user.id)
                            .withClaim("username", userLoginRequest.username)
                            .withClaim("tokenType", "refreshToken")
                            .withExpiresAt(Date(System.currentTimeMillis() +refreshTokenExpiryTime))
                            .sign(Algorithm.HMAC256(secret))
                        app.flock.social.data.dao.token.tokenDao.addToken(Token(id = user.id, accessToken = accessToken, refreshToken = refreshToken))
                        call.respond(MyToken(accessToken = accessToken, refreshToken = refreshToken))
                    }
                    else -> {
                        when {
                            user.id != null -> {
                                val accessToken = JWT.create()
                                    .withAudience(audience)
                                    .withIssuer(issuer)
                                    .withClaim("userid", user.id)
                                    .withClaim("username", userLoginRequest.username)
                                    .withClaim("tokenType", "accessToken")
                                    .withExpiresAt(Date(System.currentTimeMillis() + accessTokenExpiryTime))
                                    .sign(Algorithm.HMAC256(secret))
                                val refreshToken = JWT.create()
                                    .withAudience(audience)
                                    .withIssuer(issuer)
                                    .withClaim("userid", user.id)
                                    .withClaim("username", userLoginRequest.username)
                                    .withClaim("tokenType", "refreshToken")
                                    .withExpiresAt(Date(System.currentTimeMillis() + refreshTokenExpiryTime))
                                    .sign(Algorithm.HMAC256(secret))
                                app.flock.social.data.dao.token.tokenDao.addToken(Token(id = user.id, accessToken = accessToken, refreshToken = refreshToken))
                                call.respond(MyToken(accessToken = accessToken, refreshToken = refreshToken))
                            }
                            else -> {
                                call.respond(HttpStatusCode.BadRequest, ErrorMessage(message = "Invalid username or password"))
                            }
                        }
                    }
                }

            }
            else -> call.respond(HttpStatusCode.BadRequest, ErrorMessage(message = "Invalid username or password"))
        }
    }

    post("/signup",   {
        tags = listOf("Auth")
        description = "Performs the given operation on the given values and returns the result"
        request {
            body<UserSignUpRequest> {
                description = "User with username and password"
                required = true
            }
        }
        response {
            HttpStatusCode.Created to {
                description = "The operation was successful"
                body<SuccessMessage> {}
            }
            HttpStatusCode.BadRequest to {
                description = "Something went wrong"
                body<ErrorMessage> {
                }
            }
            HttpStatusCode.InternalServerError to {
                description = "Internal Server Error"
                body<ErrorMessage> {
                }
            }
        }

    },) {
        val userSignUpRequest = call.receive<UserSignUpRequest>()
        val isUserNameExist= userDao.getUser(username = userSignUpRequest.username)
        when {
            isUserNameExist!=null -> {
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    ErrorMessage(message = "Unable to signup due to username already reserved")
                )
            }
            else -> {
                try {
                    val isSignedUp =
                        userDao.addNewUser(username = userSignUpRequest.username, password = userSignUpRequest.password)
                    when {
                        isSignedUp != null -> {
                            call.respond(status = HttpStatusCode.Created, SuccessMessage(message = "Successfully signed up"))
                        }
                        else -> {
                            call.respond(status = HttpStatusCode.BadRequest, ErrorMessage(message = "Unable to signup"))
                        }
                    }
                } catch (e: Exception) {
                    call.respond(
                        status = HttpStatusCode.BadRequest,
                        ErrorMessage(message = "Unable to signup due to ${e.localizedMessage}")
                    )
                }
            }
        }
    }

}