package app.flock.social.plugins

import app.flock.social.data.UserLoginRequest
import app.flock.social.data.UserSignUpRequest
import app.flock.social.data.dao.user.userDao
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.requestvalidation.RequestValidation
import io.ktor.server.plugins.requestvalidation.ValidationResult

fun Application.configureRequestValidation(){
    install(RequestValidation) {
        validate<UserLoginRequest> { userrequest ->
            when {
                userrequest.username.isBlank() -> ValidationResult.Invalid("Username cannot be null")
                userrequest.password.isBlank() -> ValidationResult.Invalid("password cannot be null")
                else -> ValidationResult.Valid
            }
        }
        validate<UserSignUpRequest> { usersignuprequest ->
            when {
                usersignuprequest.username .isBlank() -> ValidationResult.Invalid("Username cannot be null")
                usersignuprequest.password.isBlank() -> ValidationResult.Invalid("password cannot be null")
                else -> when {
                    userDao.getUser(username = usersignuprequest.username)!=null -> {
                        ValidationResult.Invalid("Unable to signup due to username already reserved")
                    }
                    else -> {
                        ValidationResult.Valid
                    }
                }
            }
        }
    }
}