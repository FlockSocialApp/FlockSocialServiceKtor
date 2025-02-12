package app.flock.social.supabase

import app.flock.social.util.EnvConfig
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

val supabaseClient = createSupabaseClient(
    supabaseUrl = EnvConfig.supabaseUrl,
    supabaseKey = EnvConfig.supabaseServiceKey,
)
{
    install(Postgrest)
    install(Auth)
}

val supabaseAuth: Auth = supabaseClient.auth