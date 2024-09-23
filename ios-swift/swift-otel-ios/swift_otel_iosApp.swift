import SwiftUI

@main
struct swift_otel_iosApp: App {
    init() {
        OpenTelemetrySetup.configureOpenTelemetry()
    }
    
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
