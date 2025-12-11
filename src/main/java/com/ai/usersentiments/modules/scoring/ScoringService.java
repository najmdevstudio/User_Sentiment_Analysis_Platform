package com.ai.usersentiments.modules.scoring;

import com.ai.usersentiments.modules.providers.LlmModelFactory;
import com.ai.usersentiments.modules.providers.ModelProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScoringService {

    private final LlmModelFactory modelFactory;
    private final ScorePromptBuilder promptBuilder;
    private final ObjectMapper mapper = new ObjectMapper();

    public Score evaluate(String userMessage, String agentReply) {
        try {
            // Use a judging model like Anthropic or DeepSeek R1
            String judgeModel = "anthropic";
            ModelProvider judge = modelFactory.get(judgeModel);

            String prompt = promptBuilder.build(userMessage, agentReply);
            String raw = judge.generate(prompt);

            return mapper.readValue(raw, Score.class);

        } catch (Exception e) {
            throw new RuntimeException("Failed to evaluate response scoring", e);
        }
    }
}
