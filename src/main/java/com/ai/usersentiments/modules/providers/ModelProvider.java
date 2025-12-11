package com.ai.usersentiments.modules.providers;

public interface ModelProvider {
    String name();
    String generate(String prompt);
}
