package com.ai.usersentiments.modules.providers;

import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class LlmModelFactory {
    private final Map<String, ModelProvider> providers = new HashMap<>();

    public LlmModelFactory(List<ModelProvider> providerList) {
        providerList.forEach(p -> providers.put(p.name(), p));
    }

    public ModelProvider get(String providerKey) {
        if (providerKey == null) return providers.get("openAI");
        return providers.getOrDefault(providerKey, providers.get("OpenAI"));
    }

    public Collection<String> availableProviders() {
        return providers.keySet();
    }
    public List<ModelProvider> getAll() {
        return new ArrayList<>(providers.values());
    }
}
