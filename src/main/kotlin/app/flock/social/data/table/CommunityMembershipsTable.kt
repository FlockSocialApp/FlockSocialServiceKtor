package app.flock.social.data.table

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
import java.util.UUID

/**
 * Data transfer object representing a community membership.
 *
 * @property id Unique identifier for the membership
 * @property communityId ID of the community this membership belongs to
 * @property userId ID of the user who is a member
 * @property status Current status of the membership (pending, accepted, declined)
 */
@Serializable
data class CommunityMembershipsTableDTO(
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
    val communityId = varchar("community_id", 255)
    val userId = uuid("user_id").references(UsersTable.id)
    val status = varchar("status", 20).default("pending")
    val createdAt = datetime("created_at").clientDefault { LocalDateTime.now() }
    val updatedAt = datetime("updated_at").clientDefault { LocalDateTime.now() }

    override val primaryKey: PrimaryKey
        get() = PrimaryKey(id)
}

class CommunityMembershipsDao {
    fun getAllMemberships(): List<CommunityMembershipsTableDTO> {
        return transaction {
            CommunityMembershipsTable
                .selectAll()
                .map { membership ->
                    CommunityMembershipsTableDTO(
                        id = membership[CommunityMembershipsTable.id].toString(),
                        communityId = membership[CommunityMembershipsTable.communityId],
                        userId = membership[CommunityMembershipsTable.userId].toString(),
                        status = membership[CommunityMembershipsTable.status]
                    )
                }
        }
    }

    fun getMembershipById(id: String): CommunityMembershipsTableDTO? {
        return transaction {
            CommunityMembershipsTable
                .select { CommunityMembershipsTable.id eq UUID.fromString(id) }
                .map { membership ->
                    CommunityMembershipsTableDTO(
                        id = membership[CommunityMembershipsTable.id].toString(),
                        communityId = membership[CommunityMembershipsTable.communityId],
                        userId = membership[CommunityMembershipsTable.userId].toString(),
                        status = membership[CommunityMembershipsTable.status]
                    )
                }
                .firstOrNull()
        }
    }

    fun createMembership(membership: CommunityMembershipsTableDTO) {
        transaction {
            CommunityMembershipsTable.insert {
                it[id] = UUID.fromString(membership.id)
                it[communityId] = membership.communityId
                it[userId] = UUID.fromString(membership.userId)
                it[status] = membership.status
                it[createdAt] = LocalDateTime.now()
                it[updatedAt] = LocalDateTime.now()
            }
        }
    }

    fun updateMembership(membership: CommunityMembershipsTableDTO) {
        transaction {
            CommunityMembershipsTable.update({ CommunityMembershipsTable.id eq UUID.fromString(membership.id) }) {
                it[communityId] = membership.communityId
                it[userId] = UUID.fromString(membership.userId)
                it[status] = membership.status
                it[updatedAt] = LocalDateTime.now()
            }
        }
    }

    fun deleteMembership(id: String) {
        transaction {
            CommunityMembershipsTable.deleteWhere { CommunityMembershipsTable.id eq UUID.fromString(id) }
        }
    }
}
