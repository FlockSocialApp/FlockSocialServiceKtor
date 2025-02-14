package app.flock.social.data.dao

import app.flock.social.data.table.BookmarkDTO
import app.flock.social.data.table.BookmarksTable
import app.flock.social.data.table.CommunityApplicationFormTable
import app.flock.social.data.table.CommunityApplicationQuestionsTable
import app.flock.social.data.table.CommunityApplicationResponsesTable
import app.flock.social.data.table.CommunityApplicationSubmissionsTable
import app.flock.social.data.table.CommunityDTO
import app.flock.social.data.table.CommunityMembershipsTable
import app.flock.social.data.table.CommunityTable
import app.flock.social.data.table.EventDTO
import app.flock.social.data.table.EventsTable
import app.flock.social.data.table.FollowDTO
import app.flock.social.data.table.FollowsTable
import app.flock.social.data.table.RsvpDTO
import app.flock.social.data.table.RsvpsTable
import app.flock.social.data.table.UserDTO
import app.flock.social.data.table.UsersTable
import app.flock.social.util.EnvConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.toKotlinLocalDateTime
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

object DatabaseFactory {
    fun init() {
        val database = Database.connect(
            url = EnvConfig.databaseUrl,
            driver = "org.postgresql.Driver",
            user = EnvConfig.databaseUser,
            password = EnvConfig.databasePw
        )

       transaction(database) {
           SchemaUtils.create(
               CommunityMembershipsTable,
               CommunityApplicationFormTable,
               CommunityApplicationQuestionsTable,
               CommunityApplicationResponsesTable,
               CommunityApplicationSubmissionsTable
           )

           SchemaUtils.createMissingTablesAndColumns(
               EventsTable,
               CommunityTable,
               RsvpsTable
           )
       }
    }

    private fun initAndSeedDb(database: Database) {
        transaction(database) {
            SchemaUtils.create(
                UsersTable,
                CommunityTable,
                EventsTable,
                FollowsTable,
                RsvpsTable,
                BookmarksTable
            ).also {
                val usersDao = UsersDao()

                // Create seed data if tables are empty
                seedUserTable(usersDao)
                val demoUserId = usersDao.getAllUsers()[0].id
                val demoUserId2 = usersDao.getAllUsers()[1].id

                val communityDao = CommunityDao()
                seedCommunityTable(communityDao, demoUserId)
                val demoCommunityId = communityDao.getAllCommunities().first().id

                val eventsDao = EventDao()
                seedEventTable(eventsDao, demoCommunityId)
                val demoEventId = eventsDao.getAllEvents().first().id

                val followsDao = FollowsDao()
                seedFollowTable(followsDao, demoUserId, demoUserId2)

                val rsvpDao = RsvpDao()
                seedRsvpTable(rsvpDao, demoUserId, demoEventId)

                val bookmarkDao = BookmarkDao()
                seedBookmarkTable(bookmarkDao, demoEventId, demoUserId)
            }
        }
    }

    private fun seedBookmarkTable(
        bookmarkDao: BookmarkDao,
        demoEventId: String,
        demoUserId: String
    ) {
        if (bookmarkDao.getAllBookmarks().isEmpty()) {
            bookmarkDao.createBookmark(
                BookmarkDTO(
                    id = UUID.randomUUID().toString(),
                    eventId = demoEventId,
                    userId = demoUserId
                )
            )
        }
    }

    private fun seedRsvpTable(
        rsvpDao: RsvpDao,
        demoUserId: String,
        demoEventId: String
    ) {
        if (rsvpDao.getAllRsvps().isEmpty()) {
            rsvpDao.createRsvp(
                RsvpDTO(
                    id = UUID.randomUUID().toString(),
                    userId = demoUserId,
                    eventId = demoEventId,
                    status = "GOING"
                )
            )
        }
    }

    private fun seedFollowTable(
        followsDao: FollowsDao,
        demoUserId: String,
        demoUserId2: String
    ) {
        if (followsDao.getAllFollows().isEmpty()) {
            followsDao.createFollow(
                FollowDTO(
                    id = UUID.randomUUID().toString(),
                    followerId = demoUserId,
                    followingId = demoUserId2,
                )
            )
        }
    }

    private fun seedEventTable(eventsDao: EventDao, demoCommunityId: String) {
        if (eventsDao.getAllEvents().isEmpty()) {
            eventsDao.createEvent(
                EventDTO(
                    id = UUID.randomUUID().toString(),
                    displayName = "My Super Cool Event",
                    description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Duis tincidunt elit non dolor sodales convallis. Vivamus feugiat odio sem, vitae scelerisque nulla dignissim sit amet. Phasellus odio ligula, eleifend nec vulputate a, elementum vitae eros. Integer consectetur quam ex, eu suscipit arcu tincidunt id. Phasellus sit amet aliquet magna. Maecenas porta id metus at elementum. Etiam mauris eros, iaculis in nisl vel, consequat laoreet sem. Aenean viverra velit semper nulla congue lobortis. Aenean tristique, nunc in laoreet gravida, odio enim convallis velit, quis facilisis nibh velit quis sapien. Integer consequat metus vitae laoreet vestibulum. Phasellus in gravida diam, in ultricies ipsum. Vestibulum sollicitudin justo a bibendum accumsan. Donec placerat nisl in enim laoreet, ut consequat eros efficitur. Nam nulla quam, maximus at urna maximus, pharetra suscipit dolor",
                    communityId = demoCommunityId,
                    address = "1234 Main St",
                    startTime = java.time.LocalDateTime.now().toKotlinLocalDateTime(),
                    endTime = java.time.LocalDateTime.now().plusHours(2).toKotlinLocalDateTime(),
                    cost = null,
                    thumbnailUrl = null,
                )
            )

            eventsDao.createEvent(
                EventDTO(
                    id = UUID.randomUUID().toString(),
                    displayName = "Another Amazing Event",
                    description = "Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem.",
                    communityId = demoCommunityId,
                    address = "1234 Main St",
                    startTime = java.time.LocalDateTime.now().toKotlinLocalDateTime(),
                    endTime = java.time.LocalDateTime.now().plusHours(2).toKotlinLocalDateTime(),
                    cost = null,
                    thumbnailUrl = null,
                )
            )
        }
    }

    private fun seedCommunityTable(communityDao: CommunityDao, demoUserId: String) {
        if (communityDao.getAllCommunities().isEmpty()) {
           communityDao.createCommunity(
                CommunityDTO(
                    id = UUID.randomUUID().toString(),
                    displayName = "Flock cool kids club",
                    description = "This is a demo private community",
                    ownerId = demoUserId,
                    requiresApplication = true
                )
            )
            communityDao.createCommunity(
                CommunityDTO(
                    id = UUID.randomUUID().toString(),
                    displayName = "Flock public group",
                    description = "This is a public community",
                    ownerId = demoUserId,
                    requiresApplication = false,
                )
            )

        }
    }

    private fun seedUserTable(usersDao: UsersDao) {
        if (usersDao.getAllUsers().isEmpty()) {
            usersDao.createUser(
                UserDTO(
                    id = UUID.randomUUID().toString(),
                    "jess@flocksocial",
                    "https://picsum.photos/200",
                    "just a girl",
                )
            )

            UsersDao().createUser(
                UserDTO(
                    id = UUID.randomUUID().toString(),
                    "bryan@flocksocial",
                    "https://picsum.photos/200",
                    "silly juice boy",
                )
            )
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}