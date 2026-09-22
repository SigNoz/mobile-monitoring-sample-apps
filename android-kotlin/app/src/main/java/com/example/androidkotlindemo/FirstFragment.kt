package com.example.androidkotlindemo

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.androidkotlindemo.databinding.FragmentFirstBinding
import io.opentelemetry.api.common.AttributeKey.longKey
import io.opentelemetry.api.common.AttributeKey.stringKey
import io.opentelemetry.api.common.Attributes
import io.opentelemetry.api.logs.Severity
import io.opentelemetry.api.trace.SpanKind
import io.opentelemetry.api.trace.StatusCode

/**
 * Emits one checkout trace, with logs and a metric, when the button is tapped.
 *
 * The agent already reports the fragment lifecycle on its own. Everything below
 * is the telemetry you add yourself.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonFirst.setOnClickListener {
            Thread { runCheckout() }.start()
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
    }

    private fun runCheckout() {
        val tracer = DemoApplication.tracer() ?: return

        val checkout = tracer.spanBuilder("checkout").startSpan()
        try {
            checkout.makeCurrent().use {
                // android.util.Log calls become OpenTelemetry log records at build time.
                Log.i("CheckoutViewModel", "checkout started for user_id=usr_8f21c3")

                val cart = tracer.spanBuilder("load-cart").startSpan()
                try {
                    cart.makeCurrent().use {
                        cart.setAllAttributes(Attributes.of(longKey("cart.item_count"), 3L))
                        Thread.sleep(45)
                        Log.i("CartRepository", "cart restored: 3 items, subtotal INR 4499.00")
                    }
                } finally {
                    cart.end()
                }

                val api = tracer.spanBuilder("GET /api/v1/products")
                    .setSpanKind(SpanKind.CLIENT)
                    .startSpan()
                try {
                    api.makeCurrent().use {
                        api.setAllAttributes(
                            Attributes.builder()
                                .put(stringKey("http.request.method"), "GET")
                                .put(stringKey("url.path"), "/api/v1/products")
                                .put(longKey("http.response.status_code"), 200L)
                                .build(),
                        )
                        Thread.sleep(240)
                        Log.i("ProductApi", "GET /api/v1/products 200 in 240ms (18 items)")
                    }
                } finally {
                    api.end()
                }

                val pay = tracer.spanBuilder("authorize-payment").startSpan()
                try {
                    pay.makeCurrent().use {
                        pay.setAllAttributes(Attributes.of(stringKey("payment.method"), "card"))
                        Thread.sleep(180)
                        Log.w("PaymentGateway", "3DS challenge required for card ending 4242")
                    }
                } finally {
                    pay.end()
                }

                // A failed span, so the trace has something to investigate.
                val sync = tracer.spanBuilder("sync-order-receipt").startSpan()
                try {
                    sync.makeCurrent().use {
                        Thread.sleep(120)
                        val failure = RuntimeException("receipt service timed out")
                        sync.recordException(failure)
                        sync.setStatus(StatusCode.ERROR, "receipt sync timed out")
                        Log.e("SyncWorker", "receipt sync failed for ord_7742, will retry", failure)
                    }
                } finally {
                    sync.end()
                }

                emitOrderPlaced()
                recordMetrics()
                Log.i("CheckoutViewModel", "checkout completed for order ord_7742")
            }
        } finally {
            checkout.end()
        }
    }

    /** A log record you build yourself, with your own body and attributes. */
    private fun emitOrderPlaced() {
        DemoApplication.logger()
            ?.logRecordBuilder()
            ?.setSeverity(Severity.INFO)
            ?.setBody("order placed")
            ?.setAttribute(stringKey("order.id"), "ord_7742")
            ?.setAttribute(longKey("order.total_minor"), 449900L)
            ?.emit()
    }

    /** No bundled instrumentation records metrics, so the app records its own. */
    private fun recordMetrics() {
        val meter = DemoApplication.meter() ?: return

        meter.counterBuilder("shop.orders.placed")
            .setDescription("Orders placed from the Android app")
            .setUnit("{order}")
            .build()
            .add(1, Attributes.of(stringKey("payment.method"), "card"))

        meter.histogramBuilder("shop.checkout.duration")
            .setDescription("Checkout duration")
            .setUnit("s")
            .build()
            .record(0.585, Attributes.of(stringKey("checkout.step"), "payment"))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
