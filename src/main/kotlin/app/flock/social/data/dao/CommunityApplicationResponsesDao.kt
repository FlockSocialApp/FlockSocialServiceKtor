package app.flock.social.data.dao

import app.flock.social.data.table.CommunityApplicationResponseDTO
import app.flock.social.data.table.CommunityApplicationResponsesTable
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.time.LocalDateTime
import java.util.UUID

class CommunityApplicationResponsesDao {
    fun getAllResponses(): List<CommunityApplicationResponseDTO> {
        return transaction {
            CommunityApplicationResponsesTable
                .selectAll()
                .map { response ->
                    CommunityApplicationResponseDTO(
                        id = response[CommunityApplicationResponsesTable.id].toString(),
                        submissionId = response[CommunityApplicationResponsesTable.submissionId].toString(),
                        questionId = response[CommunityApplicationResponsesTable.question_id].toString(),
                        answer = response[CommunityApplicationResponsesTable.answer]
                    )
                }
        }
    }

    fun getResponseById(id: String): CommunityApplicationResponseDTO? {
        return transaction {
            CommunityApplicationResponsesTable
                .select { CommunityApplicationResponsesTable.id eq UUID.fromString(id) }
                .map { response ->
                    CommunityApplicationResponseDTO(
                        id = response[CommunityApplicationResponsesTable.id].toString(),
                        submissionId = response[CommunityApplicationResponsesTable.submissionId].toString(),
                        questionId = response[CommunityApplicationResponsesTable.question_id].toString(),
                        answer = response[CommunityApplicationResponsesTable.answer]
                    )
                }
                .firstOrNull()
        }
    }

    fun createResponse(response: CommunityApplicationResponseDTO) {
        transaction {
            CommunityApplicationResponsesTable.insert {
                it[id] = UUID.fromString(response.id)
                it[submissionId] = UUID.fromString(response.submissionId)
                it[question_id] = UUID.fromString(response.questionId)
                it[answer] = response.answer
                it[createdAt] = LocalDateTime.now()
                it[updatedAt] = LocalDateTime.now()
            }
        }
    }

    fun updateResponse(response: CommunityApplicationResponseDTO) {
        transaction {
            CommunityApplicationResponsesTable.update({
                CommunityApplicationResponsesTable.id eq UUID.fromString(
                    response.id
                )
            }) {
                it[submissionId] = UUID.fromString(response.submissionId)
                it[question_id] = UUID.fromString(response.questionId)
                it[answer] = response.answer
                it[updatedAt] = LocalDateTime.now()
            }
        }
    }

    fun deleteResponse(id: String) {
        transaction {
            CommunityApplicationResponsesTable.deleteWhere {
                CommunityApplicationResponsesTable.id eq UUID.fromString(
                    id
                )
            }
        }
    }
}