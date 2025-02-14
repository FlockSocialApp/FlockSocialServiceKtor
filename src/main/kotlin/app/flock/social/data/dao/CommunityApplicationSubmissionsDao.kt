package app.flock.social.data.dao

import app.flock.social.data.table.CommunityApplicationSubmissionDTO
import app.flock.social.data.table.CommunityApplicationSubmissionsTable
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.time.LocalDateTime
import java.util.UUID

class CommunityApplicationSubmissionsDao {
    fun getAllSubmissions(): List<CommunityApplicationSubmissionDTO> {
        return transaction {
            CommunityApplicationSubmissionsTable
                .selectAll()
                .map { submission ->
                    CommunityApplicationSubmissionDTO(
                        id = submission[CommunityApplicationSubmissionsTable.id].toString(),
                        communityId = submission[CommunityApplicationSubmissionsTable.communityId].toString(),
                        userId = submission[CommunityApplicationSubmissionsTable.userId].toString(),
                        formId = submission[CommunityApplicationSubmissionsTable.formId].toString(),
                        status = submission[CommunityApplicationSubmissionsTable.status]
                    )
                }
        }
    }

    fun getSubmissionById(id: String): CommunityApplicationSubmissionDTO? {
        return transaction {
            CommunityApplicationSubmissionsTable
                .select { CommunityApplicationSubmissionsTable.id eq UUID.fromString(id) }
                .map { submission ->
                    CommunityApplicationSubmissionDTO(
                        id = submission[CommunityApplicationSubmissionsTable.id].toString(),
                        communityId = submission[CommunityApplicationSubmissionsTable.communityId].toString(),
                        userId = submission[CommunityApplicationSubmissionsTable.userId].toString(),
                        formId = submission[CommunityApplicationSubmissionsTable.formId].toString(),
                        status = submission[CommunityApplicationSubmissionsTable.status]
                    )
                }
                .firstOrNull()
        }
    }

    fun createSubmission(submission: CommunityApplicationSubmissionDTO) {
        transaction {
            CommunityApplicationSubmissionsTable.insert {
                it[id] = UUID.fromString(submission.id)
                it[communityId] = UUID.fromString(submission.communityId)
                it[userId] = UUID.fromString(submission.userId)
                it[formId] = UUID.fromString(submission.formId)
                it[status] = submission.status
                it[createdAt] = LocalDateTime.now()
                it[updatedAt] = LocalDateTime.now()
            }
        }
    }

    fun updateSubmission(submission: CommunityApplicationSubmissionDTO) {
        transaction {
            CommunityApplicationSubmissionsTable.update({
                CommunityApplicationSubmissionsTable.id eq UUID.fromString(
                    submission.id
                )
            }) {
                it[communityId] = UUID.fromString(submission.communityId)
                it[userId] = UUID.fromString(submission.userId)
                it[formId] = UUID.fromString(submission.formId)
                it[status] = submission.status
                it[updatedAt] = LocalDateTime.now()
            }
        }
    }

    fun deleteSubmission(id: String) {
        transaction {
            CommunityApplicationSubmissionsTable.deleteWhere {
                CommunityApplicationSubmissionsTable.id eq UUID.fromString(
                    id
                )
            }
        }
    }
}