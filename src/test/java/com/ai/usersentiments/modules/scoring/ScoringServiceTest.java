package com.ai.usersentiments.modules.scoring;

import com.ai.usersentiments.modules.providers.LlmModelFactory;
import com.ai.usersentiments.modules.providers.ModelProvider;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ScoringServiceTest {
    @Test
    void evaluate_parsesJsonScoresCorrectly() {
        ModelProvider fakeJudge = new ModelProvider() {
            @Override public String name() { return "anthropic"; }
            @Override public String generate(String prompt) {
                return """
                    {"empathy":80,"clarity":90,"accuracy":85,"relevance":88}
                """;
            }
        };

        LlmModelFactory factory = new LlmModelFactory(List.of(fakeJudge));
        ScorePromptBuilder builder = new ScorePromptBuilder();
        ScoringService scoring = new ScoringService(factory, builder);

        Score score = scoring.evaluate("Hello", "Hi there!");

        assertThat(score.getEmpathy()).isEqualTo(80);
        assertThat(score.getClarity()).isEqualTo(90);
        assertThat(score.getAccuracy()).isEqualTo(85);
        assertThat(score.getRelevance()).isEqualTo(88);
    }
}
