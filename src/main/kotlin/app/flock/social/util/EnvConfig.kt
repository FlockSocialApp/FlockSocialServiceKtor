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
    val stripeSK: String = dotenv["STRIPE_TEST_SECRET_KEY"]
    val supabaseAnonKey: String = dotenv["SUPABASE_ANON_KEY"]
    val supabaseUrl: String = dotenv["SUPABASE_URL"]
}