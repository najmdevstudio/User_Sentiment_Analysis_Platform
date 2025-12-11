package com.ai.usersentiments.api.dto;

import com.ai.usersentiments.langgraph.model.SentimentType;

public record ChatResponse(
        String userMessage,
        SentimentType sentimentType,
        String response,
        String ticketId
) {
}
