package com.ai.usersentiments.modules.classifier;

import com.ai.usersentiments.langgraph.model.SentimentType;
import com.ai.usersentiments.modules.providers.LlmProviderRouter;
import com.ai.usersentiments.modules.providers.RoutingStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class ClassifierService {

    private final LlmProviderRouter router;


    public SentimentType classify(String message){
        Prompt prompt = new Prompt("""
                You are a sentiment classifier.
                Classify user message into:
                POSITIVE, NEGATIVE, QUERY, COMPLAINT
                No punctuation. No explanation.
                Message: "%s"
                """.formatted(message));

        // Optional: Use majority vote routing for better accuracy
        router.setStrategy(RoutingStrategy.SINGLE);
        router.setPrimary("openAI");

        // Get classification result (provider routing handles it)
        String raw = router.route(String.valueOf(prompt)).trim().toUpperCase();

        return Arrays.stream(SentimentType.values())
                .filter(s -> s.name().equals(raw))
                .findFirst()
                .orElse(SentimentType.UNKNOWN);
    }


}
