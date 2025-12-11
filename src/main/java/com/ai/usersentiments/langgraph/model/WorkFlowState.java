package com.ai.usersentiments.langgraph.model;

public record WorkFlowState(
        String userMessage,
        SentimentType sentiment,
        String ticketId,
        String agentResponse
) {
    public WorkFlowState(String userMessage) {
        this(userMessage, null, null, null);
    }

    public WorkFlowState withSentiment(SentimentType sentiment) {
        return new WorkFlowState(this.userMessage, sentiment, this.ticketId, this.agentResponse);
    }

    public WorkFlowState withTicketId(String ticketId) {
        return new WorkFlowState(this.userMessage, this.sentiment, ticketId, this.agentResponse);
    }

    public WorkFlowState withAgentResponse(String agentResponse) {
        return new WorkFlowState(this.userMessage, this.sentiment, this.ticketId, agentResponse);
    }
}
