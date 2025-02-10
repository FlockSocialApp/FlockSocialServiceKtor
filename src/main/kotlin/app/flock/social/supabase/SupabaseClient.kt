package app.flock.social.supabase

import app.flock.social.util.EnvConfig
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

val supabaseClient = createSupabaseClient(
    supabaseUrl = EnvConfig.supabaseUrl,
    supabaseKey = EnvConfig.supabaseAnonKey,
)
{
    install(Postgrest)
    install(Auth)
}