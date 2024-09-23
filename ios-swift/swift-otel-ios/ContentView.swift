import SwiftUI
import OpenTelemetryApi // Import OpenTelemetryApi for the tracer

struct ContentView: View {
    // Get a reference to the tracer
    let tracer = OpenTelemetry.instance.tracerProvider.get(instrumentationName: "SwiftExample", instrumentationVersion: "semver:0.1.0")
    
    var body: some View {
        VStack {
            Image(systemName: "globe")
                .imageScale(.large)
                .foregroundStyle(.tint)
            Text("Hello, SigNoz!")
            Button(action: {
                // Execute the doWork function when the button is pressed
                doWork()
            }) {
                Text("Do Work")
                    .padding()
                    .background(Color.blue)
                    .foregroundColor(.white)
                    .cornerRadius(10)
            }
        }
        .padding()
    }
    
    // Function to simulate work and create a span
    func doWork() {
        let sampleKey = "sampleKey"
        let sampleValue = "sampleValue"
        
        // Start a new span named "doWork"
        let childSpan = tracer.spanBuilder(spanName: "doWork")
            .setSpanKind(spanKind: .client)
            .startSpan()
        
        // Set an attribute on the span
        childSpan.setAttribute(key: sampleKey, value: sampleValue)
        
        // Simulate some work by sleeping for a random interval
        Thread.sleep(forTimeInterval: Double.random(in: 0 ..< 10) / 100)
        
        // End the span
        childSpan.end()
    }
}

#Preview {
    ContentView()
}
