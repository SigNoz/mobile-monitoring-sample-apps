package com.example.androidkotlindemo

import android.app.Application
import android.util.Log
import io.opentelemetry.android.OpenTelemetryRum
import io.opentelemetry.android.agent.OpenTelemetryRumInitializer

/**
 * Starts the OpenTelemetry Android agent once, for the whole process.
 *
 * Replace <region> and <your-ingestion-key> before you run the app.
 */
class DemoApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        rum =
            runCatching {
                OpenTelemetryRumInitializer.initialize(
                    context = this,
                    configuration = {
                        httpExport {
                            baseUrl = "https://ingest.<region>.signoz.cloud:443"
                            baseHeaders = mapOf("signoz-ingestion-key" to "<your-ingestion-key>")
                        }
                        // Export straight away so the sample is easy to verify.
                        // In production, leave buffering on so telemetry survives
                        // offline periods and process restarts.
                        diskBuffering {
                            enabled(false)
                        }
                        resource {
                            put("service.name", "sample-android-kotlin")
                            put("service.version", "1.0.0")
                        }
                    },
                )
            }.onFailure {
                Log.e(TAG, "OpenTelemetry agent failed to start", it)
            }.getOrNull()
    }

    companion object {
        const val TAG = "DemoApplication"

        /** Set once in onCreate. Null only if the agent failed to start. */
        var rum: OpenTelemetryRum? = null

        fun tracer() = rum?.openTelemetry?.getTracer("com.example.androidkotlindemo")

        fun meter() = rum?.openTelemetry?.meterProvider?.get("com.example.androidkotlindemo")

        fun logger() =
            rum?.openTelemetry?.logsBridge?.loggerBuilder("com.example.androidkotlindemo")?.build()
    }
}
