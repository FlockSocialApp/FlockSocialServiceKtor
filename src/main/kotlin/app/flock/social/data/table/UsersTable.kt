package app.flock.social.data.table

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
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

object UsersTable : Table("users") {
    val id = uuid("id")
    val displayName = varchar("display_name", 255)
    val profilePictureUrl = varchar("profile_picture_url", 255)
    val bio = text("bio")
    val createdAt = datetime("created_at").clientDefault { LocalDateTime.now() }
    val updatedAt = datetime("updated_at").clientDefault { LocalDateTime.now() }

    override val primaryKey: PrimaryKey = PrimaryKey(id)
}

fun mapRowToUserDTO(user: ResultRow): UserDTO {
    return UserDTO(
        id = user[UsersTable.id].toString(),
        displayName = user[UsersTable.displayName],
        profilePictureUrl = user[UsersTable.profilePictureUrl],
        bio = user[UsersTable.bio],
    )
}
