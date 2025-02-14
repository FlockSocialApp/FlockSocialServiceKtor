package app.flock.social.data.table

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

data class CommunityMembershipsTableDTO(
    val id: String,
    val communityId: String,
    val userId: String,
    val status: String,
)

enum class MembershipStatus(val strValue: String) {
    Pending("pending") ,
    Accepted("accepted"),
    Declined("declined"),
}

object CommunityMembershipsTable : Table("community_memberships") {
    val id = uuid("id")
    val communityId = varchar("community_id", 255)
    val userId = uuid("user_id").references(UsersTable.id)
    val status = varchar("status", 20).default("pending")
    val createdAt = datetime("created_at").clientDefault { LocalDateTime.now() }
    val updatedAt = datetime("updated_at").clientDefault { LocalDateTime.now() }

    override val primaryKey: PrimaryKey
        get() = PrimaryKey(id)
}
