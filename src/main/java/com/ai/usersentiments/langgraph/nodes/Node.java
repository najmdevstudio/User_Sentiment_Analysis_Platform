package com.ai.usersentiments.langgraph.nodes;

import com.ai.usersentiments.langgraph.model.WorkFlowState;

public interface Node {
    WorkFlowState execute(WorkFlowState state);
}
