package com.ai.usersentiments.modules.providers;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@RequiredArgsConstructor
public class LlmProviderRouter {
    private final LlmModelFactory factory;

    @Getter
    @Setter
    private RoutingStrategy strategy = RoutingStrategy.SINGLE;
    @Getter
    @Setter
    private String primary = "openAI";

    private final Map<String, Double> weights = Map.of(
            "openAI", 0.4,
            "gemini", 0.25,
            "deepseek", 0.2,
            "anthropic", 0.15
    );

    public String route(String prompt) {
        return switch (strategy) {
            case SINGLE -> factory.get(primary).generate(prompt);
            case WEIGHTED -> weightedSelection(prompt);
            case MAJORITY_VOTE -> majorityVote(prompt);
        };
    }

    private String weightedSelection(String prompt) {
        double rand = Math.random();
        double cumulative = 0.0;

        for (var entry : weights.entrySet()) {
            cumulative += entry.getValue();
            if (rand <= cumulative) {
                return factory.get(entry.getKey()).generate(prompt);
            }
        }
        return factory.get(primary).generate(prompt);
    }

    private String majorityVote(String prompt) {
        List<ModelProvider> models = factory.getAll();

        Map<String, Integer> votes = new ConcurrentHashMap<>();

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Callable<Void>> tasks = models.stream()
                    .<Callable<Void>>map(provider -> () -> {
                        String result = provider.generate(prompt);
                        String key = result == null ? "NULL" : result.trim();
                        votes.merge(key, 1, Integer::sum);
                        return null;
                    })
                    .toList();

            executor.invokeAll(tasks); // wait for all
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Voting interrupted", e);
        }

        return votes.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("UNKNOWN");
    }

}
