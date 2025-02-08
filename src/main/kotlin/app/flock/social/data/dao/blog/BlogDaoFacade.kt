package app.flock.social.data.dao.blog

import app.flock.social.data.table.blog.Blogs

interface BlogDaoFacade {
    suspend fun getBlogsByUser(userID:Int):Blogs
    suspend fun createBlog(userID: Int,title: String,description:String):Boolean
    suspend fun updateBlog(userID:Int,blogId:Int,title:String,description:String):Boolean
    suspend fun deleteBlog(userID: Int,blogId:Int):Boolean
}