package app.flock.social.data.dao

import app.flock.social.data.table.BookmarkDTO
import app.flock.social.data.table.BookmarksTable
import app.flock.social.data.table.CommunityApplicationFormDTO
import app.flock.social.data.table.CommunityApplicationFormTable
import app.flock.social.data.table.CommunityApplicationQuestionDTO
import app.flock.social.data.table.CommunityApplicationQuestionsTable
import app.flock.social.data.table.CommunityApplicationResponseDTO
import app.flock.social.data.table.CommunityApplicationResponsesTable
import app.flock.social.data.table.CommunityApplicationSubmissionDTO
import app.flock.social.data.table.CommunityApplicationSubmissionsTable
import app.flock.social.data.table.CommunityDTO
import app.flock.social.data.table.CommunityMembershipsDTO
import app.flock.social.data.table.CommunityMembershipsTable
import app.flock.social.data.table.CommunityTable
import app.flock.social.data.table.EventDTO
import app.flock.social.data.table.EventsTable
import app.flock.social.data.table.FollowDTO
import app.flock.social.data.table.FollowsTable
import app.flock.social.data.table.MembershipStatus
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
import java.time.LocalDateTime
import java.util.UUID

object DatabaseFactory {
    fun init() {
        val database = Database.connect(
            url = EnvConfig.databaseUrl,
            driver = "org.postgresql.Driver",
            user = EnvConfig.databaseUser,
            password = EnvConfig.databasePw
        )
//        clearAllTables()
//        initAndSeedDb(database)
//        transaction {
//            SchemaUtils.drop(CommunityMembershipsTable)
//            SchemaUtils.create(CommunityMembershipsTable)
//            seedAllTables()
//        }
    }

    private fun clearAllTables() {
        transaction {
            SchemaUtils.drop(
                UsersTable,
                CommunityTable,
                EventsTable,
                FollowsTable,
                RsvpsTable,
                BookmarksTable,
                CommunityApplicationFormTable,
                CommunityApplicationQuestionsTable,
                CommunityApplicationSubmissionsTable,
                CommunityApplicationResponsesTable,
                CommunityMembershipsTable
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
                BookmarksTable,
                CommunityApplicationFormTable,
                CommunityApplicationQuestionsTable,
                CommunityApplicationSubmissionsTable,
                CommunityApplicationResponsesTable,
                CommunityMembershipsTable
            ).also {
                seedAllTables()
            }
        }
    }

    fun seedAllTables() {
        val usersDao = UsersDao()

        // Create seed data if tables are empty
        seedUserTable(usersDao)
        val userId = usersDao.getAllUsers()[0].id
        val userId2 = usersDao.getAllUsers()[1].id

        val communityDao = CommunityDao()
        seedCommunityTable(communityDao, userId)
        val communityId = communityDao.getAllCommunities().first().id

        val eventsDao = EventDao()
        seedEventTable(eventsDao, communityId, userId)
        val eventId = eventsDao.getAllEvents().first().id

        val followsDao = FollowsDao()
        seedFollowTable(followsDao, userId, userId2)

        val rsvpDao = RsvpDao()
        seedRsvpTable(rsvpDao, userId, eventId)

        val bookmarkDao = BookmarkDao()
        seedBookmarkTable(bookmarkDao, eventId, userId)

        val communityApplicationFormDao = CommunityApplicationFormDao()
        val communityApplicationQuestionsDao = CommunityApplicationQuestionsDao()
        val communityApplicationResponsesDao = CommunityApplicationResponsesDao()
        val communityApplicationSubmissionDao = CommunityApplicationSubmissionsDao()
        seedCommunityApplicationFormDao(communityApplicationFormDao, communityId)
        val formId = communityApplicationFormDao.getAllForms().first().id

        seedCommunityApplicationQuestionsDao(communityApplicationQuestionsDao, formId)
        val questionId = communityApplicationQuestionsDao.getAllQuestions().first().id
        seedCommunityApplicationSubmissionsDao(communityApplicationSubmissionDao, formId, userId, communityId)
        val submissionId = communityApplicationSubmissionDao.getAllSubmissions().first().id
        seedCommunityApplicationResponsesDao(communityApplicationResponsesDao, questionId, submissionId)

        val communityMembershipsDao = CommunityMembershipsDao()
        seedCommunityMembershipTable(communityMembershipsDao, communityId, userId)
    }

    private fun seedCommunityMembershipTable(
        communityMembershipsDao: CommunityMembershipsDao,
        communityId: String,
        userId: String,
    ) {
        if (communityMembershipsDao.getAllMemberships().isEmpty()) {
            communityMembershipsDao.createMembership(
                CommunityMembershipsDTO(
                    id = UUID.randomUUID().toString(),
                    communityId = communityId,
                    userId = userId,
                    status = MembershipStatus.Accepted.strValue
                )
            )
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

    private fun seedEventTable(
        eventsDao: EventDao,
        demoCommunityId: String,
        userId: String
    ) {
        if (eventsDao.getAllEvents().isEmpty()) {
            eventsDao.createEvent(
                EventDTO(
                    id = UUID.randomUUID().toString(),
                    displayName = "My Super Cool Event",
                    description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Duis tincidunt elit non dolor sodales convallis. Vivamus feugiat odio sem, vitae scelerisque nulla dignissim sit amet. Phasellus odio ligula, eleifend nec vulputate a, elementum vitae eros. Integer consectetur quam ex, eu suscipit arcu tincidunt id. Phasellus sit amet aliquet magna. Maecenas porta id metus at elementum. Etiam mauris eros, iaculis in nisl vel, consequat laoreet sem. Aenean viverra velit semper nulla congue lobortis. Aenean tristique, nunc in laoreet gravida, odio enim convallis velit, quis facilisis nibh velit quis sapien. Integer consequat metus vitae laoreet vestibulum. Phasellus in gravida diam, in ultricies ipsum. Vestibulum sollicitudin justo a bibendum accumsan. Donec placerat nisl in enim laoreet, ut consequat eros efficitur. Nam nulla quam, maximus at urna maximus, pharetra suscipit dolor",
                    communityId = demoCommunityId,
                    address = "1234 Main St",
                    startTime = LocalDateTime.now().toKotlinLocalDateTime(),
                    endTime = LocalDateTime.now().plusHours(2).toKotlinLocalDateTime(),
                    cost = null,
                    thumbnailUrl = null,
                    ownerId = userId,
                )
            )

            eventsDao.createEvent(
                EventDTO(
                    id = UUID.randomUUID().toString(),
                    displayName = "Another Amazing Event",
                    description = "Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem.",
                    communityId = demoCommunityId,
                    address = "1234 Main St",
                    startTime = LocalDateTime.now().toKotlinLocalDateTime(),
                    endTime = LocalDateTime.now().plusHours(2).toKotlinLocalDateTime(),
                    cost = null,
                    thumbnailUrl = null,
                    ownerId = userId,
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

    private fun seedCommunityApplicationFormDao(
        communityApplicationFormDao: CommunityApplicationFormDao,
        communityId: String
    ) {
        if (communityApplicationFormDao.getAllForms().isEmpty()) {
            communityApplicationFormDao.createForm(
                CommunityApplicationFormDTO(
                    id = UUID.randomUUID().toString(),
                    communityId = communityId,
                    title = "Join the Cool Kids Club!",
                    active = true,
                )
            )
        }
    }

    private fun seedCommunityApplicationQuestionsDao(
        communityApplicationQuestionsDao: CommunityApplicationQuestionsDao,
        formId: String
    ) {
        if (communityApplicationQuestionsDao.getAllQuestions().isEmpty()) {
            communityApplicationQuestionsDao.createQuestion(
                CommunityApplicationQuestionDTO(
                    id = UUID.randomUUID().toString(),
                    applicationFormId = formId,
                    questionText = "Why do you want to join our community",
                    questionType = "text",
                    options = emptyList(),
                    order = 0,
                )
            )
        }
    }

    private fun seedCommunityApplicationResponsesDao(
        communityApplicationResponsesDao: CommunityApplicationResponsesDao,
        questionId: String,
        submissionId: String
    ) {
        if (communityApplicationResponsesDao.getAllResponses().isEmpty()) {
            communityApplicationResponsesDao.createResponse(
                CommunityApplicationResponseDTO(
                    id = UUID.randomUUID().toString(),
                    questionId = questionId,
                    submissionId = submissionId,
                    answer = "I love being part of communities and meeting new people!",
                )
            )
        }
    }

    private fun seedCommunityApplicationSubmissionsDao(
        communityApplicationSubmissionsDao: CommunityApplicationSubmissionsDao,
        formId: String,
        userId: String,
        communityId: String,
    ) {
        if (communityApplicationSubmissionsDao.getAllSubmissions().isEmpty()) {
            communityApplicationSubmissionsDao.createSubmission(
                CommunityApplicationSubmissionDTO(
                    id = UUID.randomUUID().toString(),
                    formId = formId,
                    userId = userId,
                    status = "PENDING",
                    communityId = communityId
                )
            )
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}