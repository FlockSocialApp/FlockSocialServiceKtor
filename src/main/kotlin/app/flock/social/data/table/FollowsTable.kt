package app.flock.social.data.table

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ReferenceOption
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
                .select { FollowsTable.id eq java.util.UUID.fromString(id) }
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
                it[id] = java.util.UUID.fromString(follow.id)
                it[followerId] = java.util.UUID.fromString(follow.followerId)
                it[followingId] = java.util.UUID.fromString(follow.followingId)
                it[createdAt] = LocalDateTime.now()
                it[updatedAt] = LocalDateTime.now()
            }
        }
    }

    fun updateFollow(follow: FollowDTO) {
        transaction {
            FollowsTable.update(
                { FollowsTable.id eq java.util.UUID.fromString(follow.id) }) {
                it[followerId] = java.util.UUID.fromString(follow.followerId)
                it[followingId] = java.util.UUID.fromString(follow.followingId)
                it[updatedAt] = LocalDateTime.now()
            }
        }
    }

    fun deleteFollow(id: String) {
        transaction {
            FollowsTable.deleteWhere { FollowsTable.id eq java.util.UUID.fromString(id) }
        }
    }
}


