package com.example.androidjavademo;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.androidjavademo.databinding.FragmentFirstBinding;

import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.logs.Severity;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;

/**
 * Emits one checkout trace, with logs and a metric, when the button is tapped.
 *
 * <p>The agent already reports the fragment lifecycle on its own. Everything below
 * is the telemetry you add yourself.
 */
public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonFirst.setOnClickListener(
                v -> {
                    new Thread(this::runCheckout).start();
                    NavHostFragment.findNavController(FirstFragment.this)
                            .navigate(R.id.action_FirstFragment_to_SecondFragment);
                });
    }

    private void runCheckout() {
        Tracer tracer = DemoApplication.tracer();
        if (tracer == null) {
            return;
        }

        Span checkout = tracer.spanBuilder("checkout").startSpan();
        try (Scope ignored = checkout.makeCurrent()) {
            // android.util.Log calls become OpenTelemetry log records at build time.
            Log.i("CheckoutViewModel", "checkout started for user_id=usr_8f21c3");

            Span cart = tracer.spanBuilder("load-cart").startSpan();
            try (Scope cartScope = cart.makeCurrent()) {
                cart.setAttribute("cart.item_count", 3L);
                Thread.sleep(45);
                Log.i("CartRepository", "cart restored: 3 items, subtotal INR 4499.00");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                cart.end();
            }

            Span api =
                    tracer.spanBuilder("GET /api/v1/products").setSpanKind(SpanKind.CLIENT).startSpan();
            try (Scope apiScope = api.makeCurrent()) {
                api.setAttribute("http.request.method", "GET");
                api.setAttribute("url.path", "/api/v1/products");
                api.setAttribute("http.response.status_code", 200L);
                Thread.sleep(240);
                Log.i("ProductApi", "GET /api/v1/products 200 in 240ms (18 items)");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                api.end();
            }

            Span pay = tracer.spanBuilder("authorize-payment").startSpan();
            try (Scope payScope = pay.makeCurrent()) {
                pay.setAttribute("payment.method", "card");
                Thread.sleep(180);
                Log.w("PaymentGateway", "3DS challenge required for card ending 4242");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                pay.end();
            }

            // A failed span, so the trace has something to investigate.
            Span sync = tracer.spanBuilder("sync-order-receipt").startSpan();
            try (Scope syncScope = sync.makeCurrent()) {
                Thread.sleep(120);
                RuntimeException failure = new RuntimeException("receipt service timed out");
                sync.recordException(failure);
                sync.setStatus(StatusCode.ERROR, "receipt sync timed out");
                Log.e("SyncWorker", "receipt sync failed for ord_7742, will retry", failure);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                sync.end();
            }

            emitOrderPlaced();
            recordMetrics();
            Log.i("CheckoutViewModel", "checkout completed for order ord_7742");
        } finally {
            checkout.end();
        }
    }

    /** A log record you build yourself, with your own body and attributes. */
    private void emitOrderPlaced() {
        if (DemoApplication.logger() == null) {
            return;
        }
        DemoApplication.logger()
                .logRecordBuilder()
                .setSeverity(Severity.INFO)
                .setBody("order placed")
                .setAttribute(AttributeKey.stringKey("order.id"), "ord_7742")
                .setAttribute(AttributeKey.longKey("order.total_minor"), 449900L)
                .emit();
    }

    /** No bundled instrumentation records metrics, so the app records its own. */
    private void recordMetrics() {
        Meter meter = DemoApplication.meter();
        if (meter == null) {
            return;
        }
        meter.counterBuilder("shop.orders.placed")
                .setDescription("Orders placed from the Android app")
                .setUnit("{order}")
                .build()
                .add(1, Attributes.of(AttributeKey.stringKey("payment.method"), "card"));

        meter.histogramBuilder("shop.checkout.duration")
                .setDescription("Checkout duration")
                .setUnit("s")
                .build()
                .record(0.585, Attributes.of(AttributeKey.stringKey("checkout.step"), "payment"));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
