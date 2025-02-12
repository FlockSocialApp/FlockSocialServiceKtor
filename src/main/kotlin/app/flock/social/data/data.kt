package app.flock.social.data

data class UserLoginRequest(val username: String, val password: String)
data class UserSignUpRequest(val username: String,val password: String)
data class ErrorMessage(val message:String)