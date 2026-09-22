package com.example.androidjavademo;

import android.app.Application;
import android.util.Log;

import io.opentelemetry.android.AndroidResource;
import io.opentelemetry.android.OpenTelemetryRum;
import io.opentelemetry.android.RumBuilder;
import io.opentelemetry.api.logs.Logger;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.exporter.otlp.http.logs.OtlpHttpLogRecordExporter;
import io.opentelemetry.exporter.otlp.http.metrics.OtlpHttpMetricExporter;
import io.opentelemetry.exporter.otlp.http.trace.OtlpHttpSpanExporter;
import io.opentelemetry.sdk.resources.Resource;

/**
 * Starts the OpenTelemetry Android agent once, for the whole process.
 *
 * <p>The agent's configuration DSL is Kotlin only, so this app uses RumBuilder from
 * the `core` module and builds the three OTLP exporters by hand. Instrumentation is
 * still discovered automatically, so lifecycle spans and android.util.Log capture
 * work exactly as they do in the Kotlin sample.
 *
 * <p>Replace <region> and <your-ingestion-key> before you run the app.
 */
public class DemoApplication extends Application {

    private static final String TAG = "DemoApplication";
    private static final String BASE_URL = "https://ingest.<region>.signoz.cloud:443";
    private static final String INGESTION_KEY = "<your-ingestion-key>";
    private static final String SCOPE = "com.example.androidjavademo";

    /** Set once in onCreate. Null only if the agent failed to start. */
    public static OpenTelemetryRum rum;

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            Resource resource =
                    AndroidResource.createDefault(this).toBuilder()
                            .put("service.name", "sample-android-java")
                            .put("service.version", "1.0.0")
                            .build();

            rum =
                    RumBuilder.builder(this)
                            .setResource(resource)
                            .addSpanExporterCustomizer(
                                    previous ->
                                            OtlpHttpSpanExporter.builder()
                                                    .setEndpoint(BASE_URL + "/v1/traces")
                                                    .addHeader("signoz-ingestion-key", INGESTION_KEY)
                                                    .build())
                            .addLogRecordExporterCustomizer(
                                    previous ->
                                            OtlpHttpLogRecordExporter.builder()
                                                    .setEndpoint(BASE_URL + "/v1/logs")
                                                    .addHeader("signoz-ingestion-key", INGESTION_KEY)
                                                    .build())
                            .addMetricExporterCustomizer(
                                    previous ->
                                            OtlpHttpMetricExporter.builder()
                                                    .setEndpoint(BASE_URL + "/v1/metrics")
                                                    .addHeader("signoz-ingestion-key", INGESTION_KEY)
                                                    .build())
                            .build();
        } catch (Exception e) {
            Log.e(TAG, "OpenTelemetry agent failed to start", e);
        }
    }

    public static Tracer tracer() {
        return rum == null ? null : rum.getOpenTelemetry().getTracer(SCOPE);
    }

    public static Meter meter() {
        return rum == null ? null : rum.getOpenTelemetry().getMeterProvider().get(SCOPE);
    }

    public static Logger logger() {
        return rum == null ? null : rum.getOpenTelemetry().getLogsBridge().loggerBuilder(SCOPE).build();
    }
}
