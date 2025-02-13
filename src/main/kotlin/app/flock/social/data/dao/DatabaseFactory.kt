package app.flock.social.data.dao

import app.flock.social.data.table.BookmarkDTO
import app.flock.social.data.table.BookmarkDao
import app.flock.social.data.table.BookmarksTable
import app.flock.social.data.table.CommunityDTO
import app.flock.social.data.table.CommunityDao
import app.flock.social.data.table.CommunityTable
import app.flock.social.data.table.EventDTO
import app.flock.social.data.table.EventDao
import app.flock.social.data.table.EventsTable
import app.flock.social.data.table.FollowDTO
import app.flock.social.data.table.FollowsDao
import app.flock.social.data.table.FollowsTable
import app.flock.social.data.table.RsvpDTO
import app.flock.social.data.table.RsvpDao
import app.flock.social.data.table.RsvpsTable
import app.flock.social.data.table.UserDTO
import app.flock.social.data.table.UsersDao
import app.flock.social.data.table.UsersTable
import app.flock.social.util.EnvConfig
import kotlinx.coroutines.Dispatchers
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

//       transaction(database) {
//           SchemaUtils.createMissingTablesAndColumns(
//               EventsTable
//           )
//       }
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
                val demoUserId = usersDao.getAllUsers()[0].id
                val demoUserId2 = usersDao.getAllUsers()[1].id

                val communityDao = CommunityDao()
                if (communityDao.getAllCommunities().isEmpty()) {
                    CommunityDao().createCommunity(
                        CommunityDTO(
                            id = UUID.randomUUID().toString(),
                            displayName = "Demo Community",
                            description = "This is a demo community",
                            ownerId = demoUserId,
                        )
                    )
                }
                val demoCommunityId = communityDao.getAllCommunities().first().id

                val eventsDao = EventDao()
                if (eventsDao.getAllEvents().isEmpty()) {
                    EventDao().createEvent(
                        EventDTO(
                            id = UUID.randomUUID().toString(),
                            displayName = "Demo Event",
                            description = "This is a demo event",
                            communityId = demoCommunityId,
                            address = "address",
                            startTime = null,
                            endTime = null,
                            cost = null,
                            thumbnailUrl = null,
                        )
                    )
                }
                val demoEventId = eventsDao.getAllEvents().first().id

                val followsDao = FollowsDao()
                if (followsDao.getAllFollows().isEmpty()) {
                    followsDao.createFollow(
                        FollowDTO(
                            id = UUID.randomUUID().toString(),
                            followerId = demoUserId,
                            followingId = demoUserId2,
                        )
                    )
                }

                val rsvpDao = RsvpDao()
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

                val bookmarkDao = BookmarkDao()
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
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}