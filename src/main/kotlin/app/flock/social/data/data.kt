package app.flock.social.data

data class MyToken(val accessToken: String,val refreshToken:String)
data class ErrorMessage(val message:String)
data class SuccessMessage(val message: String)