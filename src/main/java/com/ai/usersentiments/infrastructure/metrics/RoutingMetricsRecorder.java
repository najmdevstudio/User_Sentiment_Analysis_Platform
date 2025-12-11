package com.ai.usersentiments.infrastructure.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class RoutingMetricsRecorder {
    private final MeterRegistry registry;

    public RoutingMetricsRecorder(MeterRegistry registry) {
        this.registry = registry;
    }

    public void recordSuccess(String agentName) {
        registry.counter("agent_routing_success_total", "agent", agentName).increment();
    }

    public void recordFailure(String agentName) {
        registry.counter("agent_routing_failure_total", "agent", agentName).increment();
    }

    public void recordLatency(String agentName, long ms) {
        registry.timer("agent_latency_ms", "agent", agentName)
                .record(ms, java.util.concurrent.TimeUnit.MILLISECONDS);
    }
}
