package app.flock.social.data.table

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

@Serializable
data class CommunityDTO(
    val id: String,
    @SerialName("display_name")
    val displayName: String,
    val description: String?,
    @SerialName("owner_id")
    val ownerId: String,
    val requiresApplication: Boolean,
    @SerialName("banner_image_url")
    val bannerImageUrl: String?,
)

object CommunityTable : Table("communities") {
    val id = uuid("id")
    val displayName = varchar(name = "display_name", length = 255)
    val description = varchar(name = "description", length = 1000).nullable()
    val ownerId = uuid("owner_id").references(UsersTable.id)
    val requiresApplication = bool("requires_application").default(false)
    val createdAt = datetime("created_at").clientDefault { LocalDateTime.now() }
    val updatedAt = datetime("updated_at").clientDefault { LocalDateTime.now() }
    val bannerImageUrl = varchar("banner_image_url", 255).nullable()

    override val primaryKey: PrimaryKey = PrimaryKey(id)
}

fun mapRowToCommunityDTO(community: ResultRow) = CommunityDTO(
    id = community[CommunityTable.id].toString(),
    displayName = community[CommunityTable.displayName],
    description = community[CommunityTable.description],
    ownerId = community[CommunityTable.ownerId].toString(),
    requiresApplication = community[CommunityTable.requiresApplication],
    bannerImageUrl = community[CommunityTable.bannerImageUrl]
)