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
        let resources = DefaultResources().get()
        
        let instrumentationScopeName = "SwiftExample"
        let instrumentationScopeVersion = "semver:0.1.0"
        
        let otlpConfiguration: OtlpConfiguration = OtlpConfiguration(timeout: TimeInterval(10), headers: [("signoz-access-token", "xxxxxx")])
        
        let grpcChannel = ClientConnection.usingPlatformAppropriateTLS(for: MultiThreadedEventLoopGroup(numberOfThreads: 1)).connect(host: "ingest.[region].signoz.cloud", port: 443)
        
        let otlpTraceExporter = OtlpTraceExporter(channel: grpcChannel, config: otlpConfiguration)
        let stdoutExporter = StdoutExporter()
        
        let spanExporter = MultiSpanExporter(spanExporters: [otlpTraceExporter, stdoutExporter])
        
        let spanProcessor = SimpleSpanProcessor(spanExporter: spanExporter)
        OpenTelemetry.registerTracerProvider(tracerProvider:
            TracerProviderBuilder()
                .add(spanProcessor: spanProcessor)
                .build()
        )
    }
}
