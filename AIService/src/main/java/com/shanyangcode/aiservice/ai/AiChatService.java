package com.shanyangcode.aiservice.ai;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiChatService {
    @Resource
    private ChatModel chatModel;
    @Bean
    public  AiChat aichat(){
        return AiServices.create(AiChat.class, chatModel);

    }
}
