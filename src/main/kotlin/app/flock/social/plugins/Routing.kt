package app.flock.social.plugins

import app.flock.social.data.dao.BookmarkDao
import app.flock.social.data.dao.CommunityDao
import app.flock.social.data.dao.EventDao
import app.flock.social.data.dao.FollowsDao
import app.flock.social.data.dao.MailingListDao
import app.flock.social.data.dao.RsvpDao
import app.flock.social.data.dao.UsersDao
import app.flock.social.route.mailingListRoutes
import io.ktor.server.application.Application
import io.ktor.server.routing.routing
import org.slf4j.LoggerFactory

fun Application.configureRouting() {
    val eventDao = EventDao()
    val rsvpDao = RsvpDao()
    val communityDao = CommunityDao()
    val usersDao = UsersDao()
    val bookmarksDao = BookmarkDao()
    val followsDao = FollowsDao()
    val logger = LoggerFactory.getLogger(this::class.java)
    val mailingListDao = MailingListDao()

    routing {
//        paymentRoutes()
        mailingListRoutes(mailingListDao)
//        supabaseAuthRoutes()
//        eventsRoute(eventDao = eventDao,)
//        rsvpRoute(rsvpDao = rsvpDao,)
//        communityRoute(communityDao = communityDao,)
//        bookmarksRoute(bookmarksDao = bookmarksDao,)
//        followsRoute(followsDao = followsDao)
//        usersRoute(usersDao = usersDao)
    }
}
