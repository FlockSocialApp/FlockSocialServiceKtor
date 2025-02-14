package app.flock.social.data.dao

import app.flock.social.data.table.FollowDTO
import app.flock.social.data.table.FollowsTable
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.time.LocalDateTime
import java.util.UUID

class FollowsDao {
    fun getAllFollows(): List<FollowDTO> {
        return transaction {
            FollowsTable
                .selectAll()
                .map { follow ->
                    FollowDTO(
                        id = follow[FollowsTable.id].toString(),
                        followerId = follow[FollowsTable.followerId].toString(),
                        followingId = follow[FollowsTable.followingId].toString()
                    )
                }
        }
    }

    fun getFollowById(id: String): FollowDTO? {
        return transaction {
            FollowsTable
                .select { FollowsTable.id eq UUID.fromString(id) }
                .map { follow ->
                    FollowDTO(
                        id = follow[FollowsTable.id].toString(),
                        followerId = follow[FollowsTable.followerId].toString(),
                        followingId = follow[FollowsTable.followingId].toString()
                    )
                }
                .firstOrNull()
        }
    }

    fun createFollow(follow: FollowDTO) {
        transaction {
            FollowsTable.insert {
                it[id] = UUID.fromString(follow.id)
                it[followerId] = UUID.fromString(follow.followerId)
                it[followingId] = UUID.fromString(follow.followingId)
                it[createdAt] = LocalDateTime.now()
                it[updatedAt] = LocalDateTime.now()
            }
        }
    }

    fun updateFollow(follow: FollowDTO) {
        transaction {
            FollowsTable.update(
                { FollowsTable.id eq UUID.fromString(follow.id) }) {
                it[followerId] = UUID.fromString(follow.followerId)
                it[followingId] = UUID.fromString(follow.followingId)
                it[updatedAt] = LocalDateTime.now()
            }
        }
    }

    fun deleteFollow(id: String) {
        transaction {
            FollowsTable.deleteWhere { FollowsTable.id eq UUID.fromString(id) }
        }
    }
}