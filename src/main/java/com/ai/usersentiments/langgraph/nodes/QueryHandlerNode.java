package com.ai.usersentiments.langgraph.nodes;

import com.ai.usersentiments.langgraph.model.SentimentType;
import com.ai.usersentiments.langgraph.model.WorkFlowState;
import com.ai.usersentiments.modules.query.QueryResult;
import com.ai.usersentiments.modules.query.QueryService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QueryHandlerNode implements Node {

    private final QueryService queryService;

    @Override
    @Retry(name = "queryHandlerNode")
    @CircuitBreaker(name = "queryHandlerNode", fallbackMethod = "fallbackExecute")
    public WorkFlowState execute(WorkFlowState state) {

        QueryResult result = queryService.handleQuery(state.userMessage());

        return state
                .withAgentResponse(result.reply())
                .withTicketId(result.ticketId());
    }

    public WorkFlowState fallbackExecute(WorkFlowState state, Throwable ex) {
        return state.withSentiment(SentimentType.UNKNOWN)
                .withAgentResponse("We are experiencing issues processing your query right now.");
    }
}
