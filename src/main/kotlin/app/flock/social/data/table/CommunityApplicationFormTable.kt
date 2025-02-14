package app.flock.social.data.table

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

@Serializable
data class CommunityApplicationFormDTO(
    val id: String,
    @SerialName("community_id")
    val communityId: String,
    val title: String?,
    val active: Boolean
)

object CommunityApplicationFormTable : Table("community_application_form") {
    val id = uuid("id")
    val communityId = uuid("community_id").references(CommunityTable.id)
    val title = varchar("title", 255).nullable()
    val active = bool("active")
    val createdAt = datetime("created_at").clientDefault { LocalDateTime.now() }
    val updatedAt = datetime("updated_at").clientDefault { LocalDateTime.now() }

    override val primaryKey: PrimaryKey
        get() = PrimaryKey(id)
}

@Serializable
data class CommunityApplicationQuestionDTO(
    val id: String,
    val applicationFormId: String,
    val questionText: String,
    val questionType: String,
    val options: List<String>,
    val order: Int
)

object CommunityApplicationQuestionsTable : Table("community_application_questions") {
    val id = uuid("id")
    val applicationFormId = uuid("application_form_id").references(CommunityApplicationFormTable.id)
    val questionText = text("question_text")
    val questionType = varchar("question_type", 30)
    val options = text("options")
    val order = integer("order")
    val createdAt = datetime("created_at").clientDefault { LocalDateTime.now() }
    val updatedAt = datetime("updated_at").clientDefault { LocalDateTime.now() }

    override val primaryKey: PrimaryKey
        get() = PrimaryKey(id)
}

@Serializable
data class CommunityApplicationSubmissionDTO(
    val id: String,
    val communityId: String,
    val userId: String,
    val formId: String,
    val status: String,
)

object CommunityApplicationSubmissionsTable : Table("community_application_submissions") {
    val id = uuid("id")
    val communityId = uuid("community_id").references(CommunityTable.id)
    val userId = uuid("user_id").references(UsersTable.id)
    val formId = uuid("form_id").references(CommunityApplicationFormTable.id)
    val status = varchar("status", 30)
    val createdAt = datetime("created_at").clientDefault { LocalDateTime.now() }
    val updatedAt = datetime("updated_at").clientDefault { LocalDateTime.now() }

    override val primaryKey: PrimaryKey
        get() = PrimaryKey(id)
}

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