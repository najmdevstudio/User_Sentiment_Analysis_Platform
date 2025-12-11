package com.ai.usersentiments.infrastructure.persistence;

import com.ai.usersentiments.domain.WorkflowStep;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkflowStepRepository extends JpaRepository<WorkflowStep, Long> {
}
