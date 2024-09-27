// OpenTelemetrySetup.swift
import Foundation
import GRPC
import NIO
import NIOSSL
import OpenTelemetryApi
import OpenTelemetryProtocolExporterCommon
import OpenTelemetryProtocolExporterGrpc
import OpenTelemetrySdk
import ResourceExtension
import StdoutExporter

struct OpenTelemetrySetup {
    static func configureOpenTelemetry() {
        // Adding resource attributes such as service name and version
        let resource = DefaultResources().get().merging(other: Resource(attributes: [
            "service.name": AttributeValue.string("anukul-swift-test"),   // Name of your app
        ]))
        
        let otlpConfiguration: OtlpConfiguration = OtlpConfiguration(timeout: TimeInterval(10), headers: [("signoz-access-token", "xxxxxxx")])
        
        let grpcChannel = ClientConnection.usingPlatformAppropriateTLS(for: MultiThreadedEventLoopGroup(numberOfThreads: 1)).connect(host: "ingest.[region].signoz.cloud", port: 443)
        
        let otlpTraceExporter = OtlpTraceExporter(channel: grpcChannel, config: otlpConfiguration)
        let stdoutExporter = StdoutExporter()
        
        let spanExporter = MultiSpanExporter(spanExporters: [otlpTraceExporter, stdoutExporter])
        
        let spanProcessor = SimpleSpanProcessor(spanExporter: spanExporter)
        
        OpenTelemetry.registerTracerProvider(tracerProvider:
            TracerProviderBuilder()
                .add(spanProcessor: spanProcessor)
                .with(resource: resource)  // Add the resource with service name
                .build()
        )
    }
}
