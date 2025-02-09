package app.flock.social.route

import app.flock.social.stripe.PaymentResult
import app.flock.social.stripe.StripeService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Serializable
data class PaymentRequest(
    val amount: Long,
    val currency: String,
    val paymentMethodId: String
)

@Serializable
data class PaymentResponse(
    val success: Boolean,
    val paymentIntentId: String? = null,
    val clientSecret: String? = null,
    val status: String? = null,
    val error: String? = null
)

fun Route.paymentRoutes(stripeService: StripeService) {
    route("/payments") {
        get("/hello") {
            call.respond("Hello World")
        }
        post("/authorize") {
            val request = call.receive<PaymentRequest>()

            when (val result = stripeService.authorizePayment(
                amount = request.amount,
                currency = request.currency,
                paymentMethodId = request.paymentMethodId
            )) {
                is PaymentResult.Success -> {
                    call.respond(
                        HttpStatusCode.OK,
                        PaymentResponse(
                            success = true,
                            paymentIntentId = result.paymentIntentId,
                            clientSecret = result.clientSecret,
                            status = result.status
                        )
                    )
                }

                is PaymentResult.Error -> {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        PaymentResponse(
                            success = false,
                            error = result.message
                        )
                    )
                }
            }
        }
    }
}