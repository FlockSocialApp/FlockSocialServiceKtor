package app.flock.social.data.dao.user

import app.flock.social.data.UserLoginRequest
import app.flock.social.data.table.user.User

interface UserDaoFacade {
    suspend fun getAllUser(): List<User>
    suspend fun isUserAvailable(id: Int): User?

    suspend fun isUserAvailable(user:UserLoginRequest ):Boolean

    suspend fun getUser(user:UserLoginRequest):User?

    suspend fun getUser(username:String):User?
    suspend fun addNewUser(username: String, password: String): User?
    suspend fun deleteUser(id: Int): Boolean
}