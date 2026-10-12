package com.shanyangcode.aiservice.ai;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.awt.*;

@RestController
public class AiChatController {

    @Resource
    private AiChat aiChat;
    @GetMapping("/chat")
    public String chat(String prompt){
        return aiChat.chat(prompt);
    }
}
