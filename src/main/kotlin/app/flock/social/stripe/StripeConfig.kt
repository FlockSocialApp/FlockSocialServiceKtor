package app.flock.social.stripe

import com.stripe.Stripe

object StripeConfig {
    fun initialize(apiKey: String) {
        Stripe.apiKey = apiKey
    }
}