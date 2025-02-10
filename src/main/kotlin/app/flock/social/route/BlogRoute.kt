package app.flock.social.route

import app.flock.social.data.ErrorMessage
import app.flock.social.data.dao.blog.blogDao
import app.flock.social.data.table.blog.Blogs
import io.github.smiley4.ktorswaggerui.dsl.routing.delete
import io.github.smiley4.ktorswaggerui.dsl.routing.get
import io.github.smiley4.ktorswaggerui.dsl.routing.patch
import io.github.smiley4.ktorswaggerui.dsl.routing.post
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.route

data class BlogRequest(val title: String,val description:String)
data class BlogUpdateRequest(val blogId: Int, val title: String,val description:String)

fun Routing.blogRoutes() {
    authenticate("authJWT") {
        route("/blog") {
            get(
                {
                    tags = listOf("Blogs")
                    response {
                        HttpStatusCode.OK to {
                            description = "Get all Blogs"
                            body<Blogs>()

                        }
                        HttpStatusCode.InternalServerError to {
                            description = "Internal Server Error"
                            body<ErrorMessage> {
                            }
                        }
                    }
                },
            ) {
                val principal = call.principal<JWTPrincipal>()
                principal!!.payload.getClaim("username").asString()
                val userid = principal.payload.getClaim("userid").asInt()
                val blogs = blogDao.getBlogsByUser(userID = userid)
                call.respond(blogs)
            }

            post(
                {
                    tags = listOf("Blogs")
                    request {
                        body<BlogRequest> {
                            description = "Blog Create Request"
                            required = true
                        }
                    }
                    response {
                        HttpStatusCode.OK to {
                            description = "Get all blogs on this blog created"
                            body<Blogs>()

                        }

                        HttpStatusCode.BadRequest to {
                            description = "Bad Request"
                            body<ErrorMessage> {
                            }
                        }
                        HttpStatusCode.InternalServerError to {
                            description = "Internal Server Error"
                            body<ErrorMessage> {
                            }
                        }
                    }
                },
            ) {
                val principal = call.principal<JWTPrincipal>()
                principal!!.payload.getClaim("username").asString()
                val userid = principal.payload.getClaim("userid").asInt()
                val blogRequest = call.receive<BlogRequest>()
                val isBlogCreated = blogDao.createBlog(userID = userid, title = blogRequest.title, description = blogRequest.description)
                if (isBlogCreated) {
                    val blogs = blogDao.getBlogsByUser(userID = userid)
                    call.respond(blogs)
                } else {
                    call.respond(HttpStatusCode.BadRequest, ErrorMessage(message = "Unable to crate new blog"))
                }
            }

            patch(
                {
                    tags = listOf("Blogs")
                    request {
                        body<BlogUpdateRequest> {
                            description = "Blog update request"
                            required = true
                        }
                    }
                    response {
                        HttpStatusCode.OK to {
                            description = "Get all updated blogs on updated"
                            body<Blogs>()

                        }

                        HttpStatusCode.BadRequest to {
                            description = "Bad Request"
                            body<ErrorMessage> {
                            }
                        }
                        HttpStatusCode.InternalServerError to {
                            description = "Internal Server Error"
                            body<ErrorMessage> {
                            }
                        }
                    }
                },
            ) {
                val principal = call.principal<JWTPrincipal>()
                principal!!.payload.getClaim("username").asString()
                val userid = principal.payload.getClaim("userid").asInt()
                val blogupdateRequest = call.receive<BlogUpdateRequest>()
                val isBlogUpdated = blogDao.updateBlog(
                    userID = userid,
                    blogId = blogupdateRequest.blogId,
                    title = blogupdateRequest.title,
                    description = blogupdateRequest.description
                )
                if (isBlogUpdated) {
                    val blogs = blogDao.getBlogsByUser(userID = userid)
                    call.respond(blogs)
                } else {
                    call.respond(HttpStatusCode.BadRequest, ErrorMessage(message = "Unable to update blog"))
                }
            }
            delete("{id}", {
                tags = listOf("Blogs")
                request {
                    pathParameter<Int>("id") {
                        description = "id of the blog"
                        required = true
                    }
                }
                response {
                    HttpStatusCode.OK to {
                        description = "Get all updated blogs on updated"
                        body<Blogs>()

                    }

                    HttpStatusCode.NotFound to {
                        description = "Bad Request"
                        body<ErrorMessage> {
                        }
                    }
                    HttpStatusCode.InternalServerError to {
                        description = "Internal Server Error"
                        body<ErrorMessage> {
                        }
                    }
                }
            }) {
                val principal = call.principal<JWTPrincipal>()
                principal!!.payload.getClaim("username").asString()
                val userid = principal.payload.getClaim("userid").asInt()
                val blogid = call.parameters["id"]
                if (blogid != null) {
                    try {
                        val intblogid = Integer.parseInt(blogid)
                        val isBlogDeleted = blogDao.deleteBlog(userID = userid, blogId = intblogid)
                        if (isBlogDeleted) {
                            val blogs = blogDao.getBlogsByUser(userID = userid)
                            call.respond(blogs)
                        } else {
                            call.respond(HttpStatusCode.NotFound, ErrorMessage(message = "Unable to delete blog"))
                        }
                    }catch (e:NumberFormatException){
                        call.respond(HttpStatusCode.NotFound, ErrorMessage(message = "ID should be an Integer"))
                    }
                } else {
                    call.respond(HttpStatusCode.BadRequest, ErrorMessage(message = "ID should not be null"))
                }

            }
        }
    }
}