package com.ai.usersentiments.infrastructure.metrics;

import com.ai.usersentiments.domain.WorkflowExecution;
import com.ai.usersentiments.domain.WorkflowStep;
import com.ai.usersentiments.infrastructure.persistence.WorkflowExecutionRepository;
import com.ai.usersentiments.infrastructure.persistence.WorkflowStepRepository;
import com.ai.usersentiments.langgraph.model.WorkFlowState;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class WorkflowAuditService {
    private final WorkflowExecutionRepository execRepo;
    private final WorkflowStepRepository stepRepo;

    // ThreadLocal to track current execution per request thread
    private final ThreadLocal<WorkflowExecution> currentExecution = new ThreadLocal<>();
    private final ThreadLocal<AtomicInteger> stepCounter = ThreadLocal.withInitial(AtomicInteger::new);

    public void startExecution(String traceId, String userMessage) {
        WorkflowExecution execution = WorkflowExecution.builder()
                .traceId(traceId)
                .userMessage(userMessage)
                .startedAt(Instant.now())
                .success(false)
                .build();

        execRepo.save(execution);
        currentExecution.set(execution);
        stepCounter.get().set(0);
    }

    public void recordStep(String nodeName, WorkFlowState state, boolean success) {
        WorkflowExecution execution = currentExecution.get();
        if (execution == null) return;

        int order = stepCounter.get().incrementAndGet();

        WorkflowStep step = WorkflowStep.builder()
                .execution(execution)
                .stepOrder(order)
                .nodeName(nodeName)
                .sentimentAfter(state.sentiment() != null ? state.sentiment().name() : null)
                .responseAfter(state.agentResponse())
                .startedAt(Instant.now())   // you can split start/end via aspect if needed
                .endedAt(Instant.now())
                .success(success)
                .build();

        stepRepo.save(step);
    }

    public void finishExecution(WorkFlowState finalState, boolean success) {
        WorkflowExecution execution = currentExecution.get();
        if (execution == null) return;

        execution.setFinalSentiment(finalState.sentiment() != null ? finalState.sentiment().name() : null);
        execution.setFinalResponse(finalState.agentResponse());
        execution.setEndedAt(Instant.now());
        execution.setSuccess(success);
        execRepo.save(execution);

        currentExecution.remove();
        stepCounter.remove();
    }
}
