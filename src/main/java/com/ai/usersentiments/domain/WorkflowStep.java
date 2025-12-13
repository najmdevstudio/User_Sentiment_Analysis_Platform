package com.ai.usersentiments.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkflowStep {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "execution_id", nullable = false)
    private WorkflowExecution execution;

    private int stepOrder;
    private String nodeName;

    private String sentimentAfter;
    @Lob
    private String responseAfter;

    private Instant startedAt;
    private Instant endedAt;

    private boolean success;
}
