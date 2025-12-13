package com.ai.usersentiments.api;


import com.ai.usersentiments.api.dto.MetricsSummary;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("api/metrics-summary")
@RequiredArgsConstructor
public class MetricsController {

    private final MeterRegistry meterRegistry;

    @GetMapping
    public MetricsSummary metrics() {

        double total = getCounterValue("workflow_executions_total");
        double success = getCounterValue("workflow_executions_success_total");
        double complaints = getCounterValue("workflow_sentiment_total", "sentiment", "COMPLAINT");
        double latencyMs = getTimerMeanMs("workflow_execution_latency_ms");

        double successRate = total == 0 ? 0 : (success / total) * 100.0;
        double complaintRate = total == 0 ? 0 : (complaints / total) * 100.0;

        return new MetricsSummary(
                (long) total,
                successRate,
                latencyMs,
                complaintRate
        );
    }

    private double getCounterValue(String name, String... tags) {
        Counter c = meterRegistry.find(name).tags(tags).counter();
        return c != null ? c.count() : 0.0;
    }

    private double getTimerMeanMs(String name) {
        Timer t = meterRegistry.find(name).timer();
        if (t == null) {
            return 0.0;
        }
        double mean = t.mean(TimeUnit.MILLISECONDS);
        return Double.isNaN(mean) ? 0.0 : mean;
    }

}
