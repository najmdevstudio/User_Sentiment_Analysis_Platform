package com.ai.usersentiments.modules.scoring;

import lombok.Data;

@Data
public class Score {
    private int empathy;
    private int clarity;
    private int accuracy;
    private int relevance;
}
