package com.ai.usersentiments.modules.scoring;

import com.ai.usersentiments.langgraph.model.SentimentType;
import com.ai.usersentiments.langgraph.model.WorkFlowState;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MetaRoutingEvaluationService {

    private final ScoringService scoringService;
    private final MeterRegistry registry;

    public void evaluateRouting(WorkFlowState finalState) {
        String userMessage = finalState.userMessage();
        String reply = finalState.agentResponse();
        SentimentType sentiment = finalState.sentiment();

        // 1. Score the response quality
        Score score = scoringService.evaluate(userMessage, reply);

        registry.gauge("routing_empathy_score", score.getEmpathy());
        registry.gauge("routing_clarity_score", score.getClarity());
        registry.gauge("routing_accuracy_score", score.getAccuracy());

        // 2. Simple heuristic for routing correctness
        boolean routingOk = heuristicRoutingCheck(sentiment, reply);

        registry.counter("routing_correct_total").increment(routingOk ? 1 : 0);
        registry.counter("routing_incorrect_total").increment(routingOk ? 0 : 1);
    }

    private boolean heuristicRoutingCheck(SentimentType sentiment, String reply) {
        if (reply == null) return false;

        String lower = reply.toLowerCase();

        return switch (sentiment) {
            case QUERY -> lower.contains("status") || lower.contains("ticket");
            case COMPLAINT, NEGATIVE -> lower.contains("sorry") || lower.contains("apologize")
                    || lower.contains("we understand");
            case POSITIVE -> lower.contains("thank you") || lower.contains("glad");
            default -> false;
        };
    }
}
