package app.flock.social.data.dao

import app.flock.social.data.table.CommunityApplicationFormDTO
import app.flock.social.data.table.CommunityApplicationFormTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.time.LocalDateTime
import java.util.UUID

class CommunityApplicationFormDao {
    fun getAllForms(): List<CommunityApplicationFormDTO> {
        return transaction {
            CommunityApplicationFormTable
                .selectAll()
                .map { form ->
                    CommunityApplicationFormDTO(
                        id = form[CommunityApplicationFormTable.id].toString(),
                        communityId = form[CommunityApplicationFormTable.communityId].toString(),
                        title = form[CommunityApplicationFormTable.title],
                        active = form[CommunityApplicationFormTable.active]
                    )
                }
        }
    }

    fun getFormById(id: String): CommunityApplicationFormDTO? {
        return transaction {
            CommunityApplicationFormTable
                .select { CommunityApplicationFormTable.id eq UUID.fromString(id) }
                .map { form ->
                    CommunityApplicationFormDTO(
                        id = form[CommunityApplicationFormTable.id].toString(),
                        communityId = form[CommunityApplicationFormTable.communityId].toString(),
                        title = form[CommunityApplicationFormTable.title],
                        active = form[CommunityApplicationFormTable.active]
                    )
                }
                .firstOrNull()
        }
    }

    fun createForm(form: CommunityApplicationFormDTO) {
        transaction {
            CommunityApplicationFormTable.insert {
                it[id] = UUID.fromString(form.id)
                it[communityId] = UUID.fromString(form.communityId)
                it[title] = form.title
                it[active] = form.active
                it[createdAt] = LocalDateTime.now()
                it[updatedAt] = LocalDateTime.now()
            }
        }
    }

    fun updateForm(form: CommunityApplicationFormDTO) {
        transaction {
            CommunityApplicationFormTable.update({ CommunityApplicationFormTable.id eq UUID.fromString(form.id) }) {
                it[communityId] = UUID.fromString(form.communityId)
                it[title] = form.title
                it[active] = form.active
                it[updatedAt] = LocalDateTime.now()
            }
        }
    }

    fun deleteForm(id: String) {
        transaction {
            CommunityApplicationFormTable.deleteWhere { CommunityApplicationFormTable.id eq UUID.fromString(id) }
        }
    }
}