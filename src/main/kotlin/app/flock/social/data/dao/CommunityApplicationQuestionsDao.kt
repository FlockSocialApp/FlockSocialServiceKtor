package app.flock.social.data.dao

import app.flock.social.data.table.CommunityApplicationQuestionDTO
import app.flock.social.data.table.CommunityApplicationQuestionsTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.time.LocalDateTime
import java.util.UUID

class CommunityApplicationQuestionsDao {
    fun getAllQuestions(): List<CommunityApplicationQuestionDTO> {
        return transaction {
            CommunityApplicationQuestionsTable
                .selectAll()
                .map { question ->
                    CommunityApplicationQuestionDTO(
                        id = question[CommunityApplicationQuestionsTable.id].toString(),
                        applicationFormId = question[CommunityApplicationQuestionsTable.applicationFormId].toString(),
                        questionText = question[CommunityApplicationQuestionsTable.questionText],
                        questionType = question[CommunityApplicationQuestionsTable.questionType],
                        options = question[CommunityApplicationQuestionsTable.options]?.split(","),
                        order = question[CommunityApplicationQuestionsTable.order]
                    )
                }
        }
    }

    fun getQuestionById(id: String): CommunityApplicationQuestionDTO? {
        return transaction {
            CommunityApplicationQuestionsTable
                .select { CommunityApplicationQuestionsTable.id eq UUID.fromString(id) }
                .map { question ->
                    CommunityApplicationQuestionDTO(
                        id = question[CommunityApplicationQuestionsTable.id].toString(),
                        applicationFormId = question[CommunityApplicationQuestionsTable.applicationFormId].toString(),
                        questionText = question[CommunityApplicationQuestionsTable.questionText],
                        questionType = question[CommunityApplicationQuestionsTable.questionType],
                        options = question[CommunityApplicationQuestionsTable.options]?.split(","),
                        order = question[CommunityApplicationQuestionsTable.order]
                    )
                }
                .firstOrNull()
        }
    }

    fun createQuestion(question: CommunityApplicationQuestionDTO) {
        transaction {
            CommunityApplicationQuestionsTable.insert {
                it[id] = UUID.fromString(question.id)
                it[applicationFormId] = UUID.fromString(question.applicationFormId)
                it[questionText] = question.questionText
                it[questionType] = question.questionType
                it[options] = question.options?.joinToString(",")
                it[order] = question.order
                it[createdAt] = LocalDateTime.now()
                it[updatedAt] = LocalDateTime.now()
            }
        }
    }

    fun updateQuestion(question: CommunityApplicationQuestionDTO) {
        transaction {
            CommunityApplicationQuestionsTable.update({
                CommunityApplicationQuestionsTable.id eq UUID.fromString(
                    question.id
                )
            }) {
                it[applicationFormId] = UUID.fromString(question.applicationFormId)
                it[questionText] = question.questionText
                it[questionType] = question.questionType
                it[options] = question.options?.joinToString(",")
                it[order] = question.order
                it[updatedAt] = LocalDateTime.now()
            }
        }
    }

    fun deleteQuestion(id: String) {
        transaction {
            CommunityApplicationQuestionsTable.deleteWhere {
                CommunityApplicationQuestionsTable.id eq UUID.fromString(
                    id
                )
            }
        }
    }
}