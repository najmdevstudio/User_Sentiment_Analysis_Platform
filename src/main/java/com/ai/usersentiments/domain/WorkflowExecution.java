package com.ai.usersentiments.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkflowExecution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String traceId;

    @Lob
    private String userMessage;

    private String finalSentiment;
    @Lob
    private String finalResponse;

    private Instant startedAt;
    private Instant endedAt;

    private boolean success;

    @OneToMany(mappedBy = "execution", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WorkflowStep> steps = new ArrayList<>();
}
