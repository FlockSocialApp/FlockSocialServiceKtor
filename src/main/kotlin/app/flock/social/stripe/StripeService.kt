package app.flock.social.stripe

import com.stripe.exception.StripeException
import com.stripe.model.PaymentIntent
import com.stripe.param.PaymentIntentCreateParams

sealed class PaymentResult {
    data class Success(
        val paymentIntentId: String,
        val clientSecret: String,
        val status: String,
        val requiresAction: Boolean = false
    ) : PaymentResult()
    data class Error(val message: String) : PaymentResult()
}

class StripeService {
    suspend fun authorizePayment(
        amount: Long,
        currency: String,
        paymentMethodId: String
    ): PaymentResult {
        try {
            val params = PaymentIntentCreateParams.builder()
                .setAmount(amount)
                .setCurrency(currency)
                .setPaymentMethod(paymentMethodId)
                .setConfirm(true)
                .setCaptureMethod(PaymentIntentCreateParams.CaptureMethod.MANUAL)
                // Enable 3D Secure when available
                .setSetupFutureUsage(PaymentIntentCreateParams.SetupFutureUsage.OFF_SESSION)
                .build()

            val paymentIntent = PaymentIntent.create(params)

            return when (paymentIntent.status) {
                "requires_action", "requires_source_action" -> {
                    // 3D Secure authentication needed
                    PaymentResult.Success(
                        paymentIntentId = paymentIntent.id,
                        clientSecret = paymentIntent.clientSecret,
                        status = paymentIntent.status,
                        requiresAction = true
                    )
                }
                "requires_payment_method", "requires_source" -> {
                    PaymentResult.Error("The payment failed. Please try another payment method.")
                }
                else -> {
                    PaymentResult.Success(
                        paymentIntentId = paymentIntent.id,
                        clientSecret = paymentIntent.clientSecret,
                        status = paymentIntent.status,
                        requiresAction = false
                    )
                }
            }
        } catch (e: StripeException) {
            return PaymentResult.Error(e.message ?: "Payment authorization failed")
        }
    }
}