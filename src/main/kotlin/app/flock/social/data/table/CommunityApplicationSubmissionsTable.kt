package app.flock.social.data.table

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime


/**
 * Data transfer object representing a community application submission
 *
 * @property id Unique identifier for the submission
 * @property communityId ID of the community this submission is for
 * @property userId ID of the user who submitted the application
 * @property formId ID of the application form that was submitted
 * @property status Current status of the application (e.g. pending, accepted, rejected)
 */
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