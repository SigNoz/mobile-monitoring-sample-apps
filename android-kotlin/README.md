# Android Kotlin sample

An Android app instrumented with the
[OpenTelemetry Android agent](https://signoz.io/docs/instrumentation/mobile-instrumentation/opentelemetry-android/),
sending traces, logs and metrics to SigNoz.

Uses the agent's Kotlin configuration DSL.

## Run it

1. Open `app/src/main/java/com/example/androidkotlindemo/DemoApplication.kt` and replace `<region>` and `<your-ingestion-key>` with your
   [region](https://signoz.io/docs/ingestion/signoz-cloud/overview/#endpoint) and
   [ingestion key](https://signoz.io/docs/ingestion/signoz-cloud/keys/).
2. Build and install:

   ```bash
   ./gradlew assembleDebug
   adb install -r app/build/outputs/apk/debug/app-debug.apk
   ```

3. Launch the app and tap **Next** on the first screen.

## What it sends

Tapping **Next** runs a fake checkout that produces one trace:

```
checkout
├── load-cart
├── GET /api/v1/products    CLIENT, http.response.status_code=200
├── authorize-payment
└── sync-order-receipt      ERROR, records an exception
```

Alongside it:

- **Logs** from the existing `android.util.Log` calls. The `android-log` instrumentation
  rewrites them at build time, so no logging code changed. Records emitted inside a span
  carry that span's trace ID.
- **A log record** built through the OpenTelemetry logs API (`order placed`).
- **Metrics**: `shop.orders.placed` and `shop.checkout.duration`. No bundled
  instrumentation records metrics, so the app records its own.

The agent adds activity and fragment lifecycle spans on its own.

## Verify in SigNoz

Filter on `service.name = 'sample-android-kotlin'` in the Traces, Logs and Metrics explorers.
Metrics take up to 60 seconds, which is the export interval.

## Notes

- Disk buffering is turned off so the sample exports immediately. Leave it on in production
  so telemetry survives offline periods.
- Requires AGP 9.1.0+ and `compileSdk` 37, which the agent's dependencies pull in.
- Uninstall with `adb uninstall com.example.androidkotlindemo`.
