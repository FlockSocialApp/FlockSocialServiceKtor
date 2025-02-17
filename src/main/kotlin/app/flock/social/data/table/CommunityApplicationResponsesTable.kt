package app.flock.social.data.table

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

/**
 * Data transfer object representing a response to a question in a community application
 *
 * @property id Unique identifier for the response
 * @property submissionId ID of the application submission this response belongs to
 * @property questionId ID of the question being answered
 * @property answer The user's answer to the question
 */
@Serializable
data class CommunityApplicationResponseDTO(
    val id: String,
    val submissionId: String,
    val questionId: String,
    val answer: String
)

object CommunityApplicationResponsesTable : Table("community_applications_responses") {
    val id = uuid("id")
    val submissionId = uuid("submission_id").references(CommunityApplicationSubmissionsTable.id)
    val question_id = uuid("question_id").references(CommunityApplicationQuestionsTable.id)
    val answer = text("answer")
    val createdAt = datetime("created_at").clientDefault { LocalDateTime.now() }
    val updatedAt = datetime("updated_at").clientDefault { LocalDateTime.now() }

    override val primaryKey: PrimaryKey
        get() = PrimaryKey(id)
}