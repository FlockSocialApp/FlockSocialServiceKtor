package app.flock.social.data.dao

import app.flock.social.data.table.UserDTO
import app.flock.social.data.table.UsersTable
import app.flock.social.data.table.mapRowToUserDTO
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.time.LocalDateTime
import java.util.UUID

class UsersDao {
    fun getAllUsers(): List<UserDTO> {
        return transaction {
            UsersTable
                .selectAll()
                .map { user ->
                    mapRowToUserDTO(user)
                }
        }
    }

    fun getUserById(id: String): UserDTO? {
        return transaction {
            UsersTable
                .select { UsersTable.id eq UUID.fromString(id) }
                .map { user ->
                    mapRowToUserDTO(user)
                }
                .firstOrNull()
        }
    }

    fun createUser(user: UserDTO) {
        transaction {
            UsersTable.insert {
                it[id] = UUID.fromString(user.id)
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
            UsersTable.update({ UsersTable.id eq UUID.fromString(user.id) }) {
                it[displayName] = user.displayName
                it[profilePictureUrl] = user.profilePictureUrl
                it[bio] = user.bio
                it[updatedAt] = LocalDateTime.now()
            }
        }
    }

    fun deleteUser(id: String) {
        transaction {
            UsersTable.deleteWhere { UsersTable.id eq UUID.fromString(id) }
        }
    }
}