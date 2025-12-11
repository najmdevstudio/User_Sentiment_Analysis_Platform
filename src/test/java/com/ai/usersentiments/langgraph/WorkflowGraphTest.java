package com.ai.usersentiments.langgraph;

import com.ai.usersentiments.langgraph.model.SentimentType;
import com.ai.usersentiments.langgraph.model.WorkFlowState;
import com.ai.usersentiments.langgraph.nodes.ClassifierNode;
import com.ai.usersentiments.langgraph.nodes.FeedbackHandlerNode;
import com.ai.usersentiments.langgraph.nodes.QueryHandlerNode;
import com.ai.usersentiments.modules.scoring.MetaRoutingEvaluationService;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class WorkflowGraphTest {
    @Test
    void workflow_routesComplaintToFeedbackNodeAndCallsMetaAgent() {
        ClassifierNode classifierNode = mock(ClassifierNode.class);
        FeedbackHandlerNode feedbackNode = mock(FeedbackHandlerNode.class);
        QueryHandlerNode queryNode = mock(QueryHandlerNode.class);
        MetaRoutingEvaluationService metaService = mock(MetaRoutingEvaluationService.class);

        WorkflowGraph graph = new WorkflowGraph(classifierNode, feedbackNode, queryNode, metaService);

        WorkFlowState initial = new WorkFlowState("Service was terrible!");
        WorkFlowState afterClassification = initial.withSentiment(SentimentType.COMPLAINT);
        WorkFlowState afterFeedback = afterClassification.withAgentResponse("Sorry, your ticket ID is ABC123");

        when(classifierNode.execute(any())).thenReturn(afterClassification);
        when(feedbackNode.execute(afterClassification)).thenReturn(afterFeedback);

        WorkFlowState finalState = graph.run("Service was terrible!");

        assertThat(finalState.sentiment()).isEqualTo(SentimentType.COMPLAINT);
        assertThat(finalState.agentResponse()).contains("ticket ID");

        verify(feedbackNode).execute(afterClassification);
        verify(queryNode, never()).execute(any());
        verify(metaService).evaluateRouting(finalState);
    }
}
