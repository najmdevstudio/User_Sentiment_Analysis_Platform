package com.ai.usersentiments.langgraph.nodes;

import com.ai.usersentiments.langgraph.model.SentimentType;
import com.ai.usersentiments.langgraph.model.WorkFlowState;
import com.ai.usersentiments.modules.feedback.FeedBackResult;
import com.ai.usersentiments.modules.feedback.FeedbackService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FeedbackHandlerNode implements Node{
    private final FeedbackService feedbackService;

    @Override
    @Retry(name = "feedbackHandlerNode")
    @CircuitBreaker(name = "feedbackHandlerNode", fallbackMethod = "fallbackExecute")
    public WorkFlowState execute(WorkFlowState state){
        FeedBackResult reply = feedbackService.handleComplaint(state.userMessage());
        return state
                .withAgentResponse(reply.reply())
                .withTicketId(reply.ticketId());
    }

    public WorkFlowState fallbackExecute(WorkFlowState state, Throwable ex) {
        return state.withSentiment(SentimentType.UNKNOWN)
                .withAgentResponse("We are experiencing issues processing your complaint right now.");
    }
}
