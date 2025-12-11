package com.ai.usersentiments.api;

import com.ai.usersentiments.api.dto.ChatRequest;
import com.ai.usersentiments.api.dto.ChatResponse;
import com.ai.usersentiments.langgraph.WorkflowGraph;
import com.ai.usersentiments.langgraph.model.WorkFlowState;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Tag(name = "Chat", description = "Customer sentiment and ticket interaction API")
public class ChatController {
    private final WorkflowGraph workflowGraph;

    @Operation(summary = "Submit a chat message", description = "Routes message through classifier, feedback or query agents.")
    @PostMapping
    public ChatResponse handleChat(@RequestBody ChatRequest request) {
        log.info("Received chat message: {}", request.message());

        WorkFlowState state = workflowGraph.run(request.message());

        return new ChatResponse(
                state.userMessage(),
                state.sentiment(),
                state.agentResponse(),
                state.ticketId()
        );
    }
}
