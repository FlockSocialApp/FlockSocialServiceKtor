package app.flock.social.data.table

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

@Serializable
data class FollowDTO(
    val id: String,
    @SerialName("follower_id")
    val followerId: String,
    @SerialName("following_id")
    val followingId: String
)

object FollowsTable : Table("user_follows") {
    val id = uuid("id")
    val followerId = uuid("follower_id").references(UsersTable.id, onDelete = ReferenceOption.CASCADE)
    val followingId = uuid("following_id").references(UsersTable.id, onDelete = ReferenceOption.CASCADE)
    val createdAt = datetime("created_at").clientDefault { LocalDateTime.now() }
    val updatedAt = datetime("updated_at").clientDefault { LocalDateTime.now() }

    override val primaryKey: PrimaryKey = PrimaryKey(id)
}


