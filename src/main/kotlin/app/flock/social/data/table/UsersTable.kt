package app.flock.social.data.table

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.time.LocalDateTime

@Serializable
data class UserDTO(
    val id: String,
    @SerialName("display_name")
    val displayName: String,
    @SerialName("profile_picture_url")
    val profilePictureUrl: String,
    val bio: String,
)

object UsersTable : Table("users1") {
    val id = uuid("id")
    val displayName = varchar("display_name", 255)
    val profilePictureUrl = varchar("profile_picture_url", 255)
    val bio = text("bio")
    val createdAt = datetime("created_at").clientDefault { LocalDateTime.now() }
    val updatedAt = datetime("updated_at").clientDefault { LocalDateTime.now() }

    override val primaryKey: PrimaryKey = PrimaryKey(id)
}

class UserDao {
    fun getAllUsers(): List<UserDTO> {
        return UsersTable
            .selectAll()
            .map { user ->
                UserDTO(
                    id = user[UsersTable.id].toString(),
                    displayName = user[UsersTable.displayName],
                    profilePictureUrl = user[UsersTable.profilePictureUrl],
                    bio = user[UsersTable.bio],
                )
            }
    }

    fun getUserById(id: String): UserDTO? {
        return UsersTable
            .select { UsersTable.id eq java.util.UUID.fromString(id) }
            .map { user ->
                UserDTO(
                    id = user[UsersTable.id].toString(),
                    displayName = user[UsersTable.displayName],
                    profilePictureUrl = user[UsersTable.profilePictureUrl],
                    bio = user[UsersTable.bio],
                )
            }
            .firstOrNull()
    }

    fun createUser(user: UserDTO) {
        transaction {
            UsersTable.insert {
                it[id] = java.util.UUID.fromString(user.id)
                it[displayName] = user.displayName
                it[profilePictureUrl] = user.profilePictureUrl
                it[bio] = user.bio
                it[createdAt] = LocalDateTime.now()
                it[updatedAt] = LocalDateTime.now()
            }
        }
    }

    fun updateUser(user: UserDTO) {
        transaction {
            UsersTable.update({ UsersTable.id eq java.util.UUID.fromString(user.id) }) {
                it[displayName] = user.displayName
                it[profilePictureUrl] = user.profilePictureUrl
                it[bio] = user.bio
                it[updatedAt] = LocalDateTime.now()
            }
        }
    }

    fun deleteUser(id: String) {
        transaction {
            UsersTable.deleteWhere { UsersTable.id eq java.util.UUID.fromString(id) }
        }
    }
}