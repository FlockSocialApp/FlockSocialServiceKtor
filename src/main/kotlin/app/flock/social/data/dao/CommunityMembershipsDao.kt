package app.flock.social.data.dao

import app.flock.social.data.table.CommunityMembershipsDTO
import app.flock.social.data.table.CommunityMembershipsTable
import app.flock.social.data.table.MembershipStatus
import app.flock.social.data.table.UserDTO
import app.flock.social.data.table.UsersTable
import app.flock.social.data.table.mapRowToUserDTO
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.time.LocalDateTime
import java.util.UUID

class CommunityMembershipsDao {
    fun getAllMemberships(): List<CommunityMembershipsDTO> {
        return transaction {
            CommunityMembershipsTable
                .selectAll()
                .map { membership ->
                    mapToCommunityMembershipsDTO(membership)
                }
        }
    }

    fun getMembershipById(id: String): CommunityMembershipsDTO? {
        return transaction {
            CommunityMembershipsTable
                .select { CommunityMembershipsTable.id eq UUID.fromString(id) }
                .map { membership ->
                    mapToCommunityMembershipsDTO(membership)
                }
                .firstOrNull()
        }
    }

    @Serializable
    data class CommunityMembershipsResponse(
        val members: List<UserDTO>,
        val waitlist: List<UserDTO>,
        val pending: List<UserDTO>,
    )

    fun getMembershipsForCommunityId(communityId: String): CommunityMembershipsResponse {
        return transaction {
            val memberships: Map<MembershipStatus, List<UserDTO>> = (CommunityMembershipsTable innerJoin UsersTable)
                .select { CommunityMembershipsTable.communityId eq UUID.fromString(communityId) }
                .map { row ->
                    val user = mapRowToUserDTO(row)
                    val membership = mapToCommunityMembershipsDTO(row)
                    Pair(user, membership)
                }.groupBy(
                    keySelector = { (_, membership) -> MembershipStatus.fromString(membership.status) },
                    valueTransform = {
                        it.first
                    }
                )

            CommunityMembershipsResponse(
                members = memberships[MembershipStatus.Accepted].orEmpty(),
                waitlist = memberships[MembershipStatus.Waitlisted].orEmpty(),
                pending = memberships[MembershipStatus.Pending].orEmpty(),
            )
        }
    }

    private fun mapToCommunityMembershipsDTO(membership: ResultRow) =
        CommunityMembershipsDTO(
            id = membership[CommunityMembershipsTable.id].toString(),
            communityId = membership[CommunityMembershipsTable.communityId].toString(),
            userId = membership[CommunityMembershipsTable.userId].toString(),
            status = membership[CommunityMembershipsTable.status]
        )

    fun createMembership(membership: CommunityMembershipsDTO) {
        transaction {
            CommunityMembershipsTable.insert {
                it[id] = UUID.fromString(membership.id)
                it[communityId] = UUID.fromString(membership.communityId)
                it[userId] = UUID.fromString(membership.userId)
                it[status] = membership.status
                it[createdAt] = LocalDateTime.now()
                it[updatedAt] = LocalDateTime.now()
            }
        }
    }

    fun updateMembership(membership: CommunityMembershipsDTO) {
        transaction {
            CommunityMembershipsTable.update({ CommunityMembershipsTable.id eq UUID.fromString(membership.id) }) {
                it[communityId] = UUID.fromString(membership.communityId)
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