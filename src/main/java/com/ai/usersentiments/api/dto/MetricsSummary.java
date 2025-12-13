package com.ai.usersentiments.api.dto;

public record MetricsSummary(
        long totalConversations,
        double successRate,
        double avgLatencyMs,
        double complaintRate
) {
}
