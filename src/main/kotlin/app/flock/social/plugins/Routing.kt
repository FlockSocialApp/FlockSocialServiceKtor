package app.flock.social.plugins

import app.flock.social.data.table.BookmarkDao
import app.flock.social.data.table.CommunityDao
import app.flock.social.data.table.EventDao
import app.flock.social.data.table.FollowsDao
import app.flock.social.data.table.RsvpDao
import app.flock.social.data.table.UsersDao
import app.flock.social.route.bookmarksRoute
import app.flock.social.route.communityRoute
import app.flock.social.route.eventsRoute
import app.flock.social.route.followsRoute
import app.flock.social.route.mailingListRoutes
import app.flock.social.route.paymentRoutes
import app.flock.social.route.rsvpRoute
import app.flock.social.route.supabaseAuthRoutes
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

    routing {
        paymentRoutes()
        mailingListRoutes()
        supabaseAuthRoutes()

        eventsRoute(
            eventDao = eventDao,
        )

        rsvpRoute(
            rsvpDao = rsvpDao,
        )

        communityRoute(
            communityDao = communityDao,
        )

        bookmarksRoute(
            bookmarksDao = bookmarksDao,
            usersDao = usersDao
        )

        followsRoute(
            followsDao = followsDao
        )
    }
}
