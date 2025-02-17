package app.flock.social.data.table

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

/**
 * Data transfer object representing a question in a community application form.
 *
 * @property id Unique identifier for the question
 * @property applicationFormId ID of the application form this question belongs to
 * @property questionText The text content of the question
 * @property questionType The type of question (e.g. text, multiple choice)
 * @property options List of possible answer options for multiple choice questions
 * @property order Integer indicating the display order of this question in the form
 */
@Serializable
data class CommunityApplicationQuestionDTO(
    val id: String,
    val applicationFormId: String,
    val questionText: String,
    val questionType: String,
    val options: List<String>?,
    val order: Int
)

object CommunityApplicationQuestionsTable : Table("community_application_questions") {
    val id = uuid("id")
    val applicationFormId = uuid("application_form_id").references(CommunityApplicationFormTable.id)
    val questionText = text("question_text")
    val questionType = varchar("question_type", 30)
    val options = text("options").nullable()
    val order = integer("order")
    val createdAt = datetime("created_at").clientDefault { LocalDateTime.now() }
    val updatedAt = datetime("updated_at").clientDefault { LocalDateTime.now() }

    override val primaryKey: PrimaryKey
        get() = PrimaryKey(id)
}