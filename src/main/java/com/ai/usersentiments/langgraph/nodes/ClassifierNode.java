package com.ai.usersentiments.langgraph.nodes;

import com.ai.usersentiments.langgraph.model.SentimentType;
import com.ai.usersentiments.langgraph.model.WorkFlowState;
import com.ai.usersentiments.modules.classifier.ClassifierService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClassifierNode implements Node{
    private final ClassifierService classifierService;

    @Override
    @Retry(name = "classifierNode")
    @CircuitBreaker(name = "classifierNode", fallbackMethod = "fallbackExecute")
    public WorkFlowState execute(WorkFlowState state) {
        SentimentType sentiment = classifierService.classify(state.userMessage());

        return state.withSentiment(sentiment);
    }

    public WorkFlowState fallbackExecute(WorkFlowState state, Throwable ex) {
        return state.withSentiment(SentimentType.UNKNOWN)
                .withAgentResponse("We are experiencing issues processing your message right now.");
    }
}
