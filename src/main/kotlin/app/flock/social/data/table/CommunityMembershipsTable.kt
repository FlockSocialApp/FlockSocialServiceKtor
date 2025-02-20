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
    val status: String
)

/**
 * Enum representing the possible statuses of a community membership.
 *
 * @property strValue The string representation of the status
 */
@Serializable
enum class MembershipStatus(val strValue: String) {
    /**
     * User is pending review of their application
     */
    Pending("pending"),

    /**
     * User application has been reviewed but not accepted into the club yet due to *REASONS*
     */
    Waitlisted("waitlisted"),

    /**
     * User application has been accepted
     */
    Accepted("accepted"),

    /**
     * User has been declined
     */
    Declined("declined"),

    /**
     * no fuckin clue
     */
    Unknown("unknown");

    companion object {
        fun fromString(str: String): MembershipStatus {
            return entries.firstOrNull {
                it.strValue == str
            } ?: Unknown
        }
    }
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

