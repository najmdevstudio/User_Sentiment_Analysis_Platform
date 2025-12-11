package com.ai.usersentiments.modules.providers;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class DeepSeekProvider implements ModelProvider{

    private final ChatModel deepSeekChatModel;

    public DeepSeekProvider(@Qualifier("deepSeekChatModel") ChatModel chatModel) {
        this.deepSeekChatModel = chatModel;
    }

    @Override
    public String name(){
        return "deepseek";
    }

    @Override
    public String generate(String prompt){
        return deepSeekChatModel.call(prompt);
    }
}
