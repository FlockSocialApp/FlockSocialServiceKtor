package app.flock.social.util

import io.github.cdimascio.dotenv.Dotenv

object EnvConfig {
    private fun loadEnv(): Dotenv {
        return Dotenv.configure()
            .directory("./") // Adjust if your .env file is elsewhere
            .ignoreIfMalformed()
            .ignoreIfMissing()
            .load()
    }

    private val dotenv = loadEnv()

    // env vars
    val env: String = dotenv["ENV"]
    val stripeSK: String = dotenv["STRIPE_TEST_SECRET_KEY"]
    val supabaseAnonKey: String = dotenv["SUPABASE_ANON_KEY"]
    val supabaseServiceKey: String = dotenv["SUPABASE_SERVICE_KEY"]
    val supabaseUrl: String = dotenv["SUPABASE_URL"]
    val databaseUrl: String = dotenv["DATABASE_URL"]
    val databaseUser: String = dotenv["DATABASE_USER"]
    val databasePw: String = if (env == "dev") "" else dotenv["DATABASE_PASSWORD"]
    val jwtSecret: String = dotenv["SUPABASE_JWT_SECRET"]
    val jwtIssuer: String = dotenv["SUPABASE_JWT_ISSUER"]
}