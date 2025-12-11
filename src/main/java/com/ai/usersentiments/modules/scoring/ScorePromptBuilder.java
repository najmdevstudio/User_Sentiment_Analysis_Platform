package com.ai.usersentiments.modules.scoring;

import org.springframework.stereotype.Component;

@Component
public class ScorePromptBuilder {
    public String build(String userMessage, String agentReply) {
        return """
            You are analyzing **user experience quality**, as provided by the assistant.

            USER MESSAGE:
            %s

            ASSISTANT RESPONSE:
            %s

            Your task is to estimate four attributes with a score between 0 and 100 for each category:
            - empathy: How emotionally aware and sensitive the response is.
            - clarity: How easy it is to understand.
            - accuracy: Does it correctly address the user's question?
            - relevance: Does it match user intent?

            Return ONLY this JSON structure:
            {"empathy": <int>, "clarity": <int>, "accuracy": <int>, "relevance": <int>}
            """.formatted(userMessage, agentReply);
    }
}
