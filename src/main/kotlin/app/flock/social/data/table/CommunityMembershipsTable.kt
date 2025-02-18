package app.flock.social.data.table

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

/**
 * Data transfer object representing a community membership.
 *
 * @property id Unique identifier for the membership
 * @property communityId ID of the community this membership belongs to
 * @property userId ID of the user who is a member
 * @property status Current status of the membership (pending, accepted, declined)
 */
@Serializable
data class CommunityMembershipsDTO(
    val id: String,
    val communityId: String,
    val userId: String,
    val status: String,
)

/**
 * Enum representing the possible statuses of a community membership.
 *
 * @property strValue The string representation of the status
 */
@Serializable
enum class MembershipStatus(val strValue: String) {
    Pending("pending"),
    Accepted("accepted"),
    Declined("declined"),
}

object CommunityMembershipsTable : Table("community_memberships") {
    val id = uuid("id")
    val communityId = uuid("community_id").references(CommunityTable.id)
    val userId = uuid("user_id").references(UsersTable.id)
    val status = varchar("status", 20).default("pending")
    val createdAt = datetime("created_at").clientDefault { LocalDateTime.now() }
    val updatedAt = datetime("updated_at").clientDefault { LocalDateTime.now() }

    override val primaryKey: PrimaryKey
        get() = PrimaryKey(id)
}

