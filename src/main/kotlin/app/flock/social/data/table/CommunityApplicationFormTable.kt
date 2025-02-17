package app.flock.social.data.table

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

/**
 * Data transfer object representing a community application form.
 *
 * @property id Unique identifier for the application form
 * @property communityId ID of the community this form belongs to
 * @property title Optional title of the application form
 * @property active Whether this form is currently active and accepting submissions
 */
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
