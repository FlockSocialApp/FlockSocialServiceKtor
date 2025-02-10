package app.flock.social.data.dao.user

import app.flock.social.data.UserLoginRequest
import app.flock.social.data.dao.DatabaseFactory.dbQuery
import app.flock.social.data.table.user.User
import app.flock.social.data.table.user.UsersTable
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll

class UserDAOFacadeImpl : UserDaoFacade {

    private fun resultRowToArticle(row: ResultRow) = User(
        id = row[UsersTable.id],
        username = row[UsersTable.username],
        password = row[UsersTable.password],
    )
    override suspend fun getAllUser(): List<User> =dbQuery {
        UsersTable.selectAll().map(::resultRowToArticle)
    }
    override suspend fun isUserAvailable(user:UserLoginRequest ):Boolean=dbQuery {
      UsersTable
          .select { (UsersTable.username eq user.username) and (UsersTable.password eq user.password)}
          .map(::resultRowToArticle)
          .isNotEmpty()
  }

    override suspend fun getUser(user: UserLoginRequest): User? = dbQuery {
        UsersTable
            .select { (UsersTable.username eq user.username) and (UsersTable.password eq user.password)}
            .map(::resultRowToArticle)
            .singleOrNull()
    }

    override suspend fun getUser(username: String): User?= dbQuery {
        UsersTable
            .select { (UsersTable.username eq username) }
            .map(::resultRowToArticle).firstOrNull()

    }

    override suspend fun isUserAvailable(id: Int): User? = dbQuery {
        UsersTable
            .select { UsersTable.id eq id }
            .map(::resultRowToArticle)
            .singleOrNull()
    }

    override suspend fun addNewUser(username: String, password: String): User? = dbQuery {
        val insertStatement = UsersTable.insert {
            it[UsersTable.username] = username
            it[UsersTable.password] = password
        }
        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToArticle)
    }

    override suspend fun deleteUser(id: Int): Boolean =dbQuery {
        UsersTable.deleteWhere { UsersTable.id eq id } > 0
    }
}
val userDao: UserDaoFacade = UserDAOFacadeImpl().apply {
    runBlocking {
        if(getAllUser().isEmpty()) {
            addNewUser(username = "Arjun", password = "password")

        }
    }
}