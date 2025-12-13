package com.ai.usersentiments.infrastructure.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class WorkFlowMetricsRecorder {
    private final MeterRegistry registry;

    public void recordExecution(boolean success, String sentiment, long latencyMs) {
        // 1. Total executions
        registry.counter("workflow_executions_total").increment();

        // 2. Successful executions
        if (success) {
            registry.counter("workflow_executions_success_total").increment();
        }

        // 3. Sentiment distribution
        if (sentiment != null) {
            registry.counter("workflow_sentiment_total", "sentiment", sentiment)
                    .increment();
        }

        // 4. Latency
        registry.timer("workflow_execution_latency_ms")
                .record(latencyMs, TimeUnit.MILLISECONDS);
    }
}
