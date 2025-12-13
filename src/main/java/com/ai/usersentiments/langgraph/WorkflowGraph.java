package com.ai.usersentiments.langgraph;

import com.ai.usersentiments.langgraph.model.SentimentType;
import com.ai.usersentiments.langgraph.model.WorkFlowState;
import com.ai.usersentiments.langgraph.nodes.ClassifierNode;
import com.ai.usersentiments.langgraph.nodes.FeedbackHandlerNode;
import com.ai.usersentiments.langgraph.nodes.QueryHandlerNode;
import com.ai.usersentiments.modules.scoring.MetaRoutingEvaluationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WorkflowGraph {

    private final ClassifierNode classifierNode;
    private final FeedbackHandlerNode feedbackHandlerNode;
    private final QueryHandlerNode queryHandlerNode;
    private final MetaRoutingEvaluationService metaRoutingEvaluationService;

    public WorkFlowState run(String userMessage) {


        WorkFlowState state = new WorkFlowState(userMessage);


        state = classifierNode.execute(state);



        if (looksLikeTicketQuery(userMessage)) {
            state = state.withSentiment(SentimentType.QUERY);
        }


        SentimentType sentiment = state.sentiment();

        WorkFlowState finalState = switch (sentiment) {
            case POSITIVE -> state.withAgentResponse("Thank you for your positive message!");
            case NEGATIVE, COMPLAINT -> feedbackHandlerNode.execute(state);
            case QUERY -> queryHandlerNode.execute(state);
            default -> state.withAgentResponse("I'm not sure how to help with that.");
        };


        metaRoutingEvaluationService.evaluateRouting(finalState);

        return finalState;
    }
    private boolean looksLikeTicketQuery(String message) {
        String lower = message.toLowerCase();
        boolean mentionsTicket = lower.contains("ticket");
        boolean mentionsCloseOrStatus = lower.contains("close") ||
                lower.contains("status") ||
                lower.contains("update");
        boolean hasIdPattern = message.matches("(?s).*\\b[0-9a-fA-F]{8}\\b.*");
        return mentionsTicket && (mentionsCloseOrStatus || hasIdPattern);
    }
}
