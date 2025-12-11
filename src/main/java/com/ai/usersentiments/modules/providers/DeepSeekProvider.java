package com.ai.usersentiments.modules.providers;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeepSeekProvider implements ModelProvider{

    private final ChatModel deepSeekChatModel;

    @Override
    public String name(){
        return "DeepSeek";
    }

    @Override
    public String generate(String prompt){
        return deepSeekChatModel.call(prompt);
    }
}
