package com.ai.usersentiments.infrastructure.persistence;

import com.ai.usersentiments.domain.WorkflowExecution;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkflowExecutionRepository extends JpaRepository<WorkflowExecution, Long> {
}
