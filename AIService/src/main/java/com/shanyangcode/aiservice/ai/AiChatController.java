package com.shanyangcode.aiservice.ai;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class AiChatController {

    @Resource
    private AiChat aiChat;

    @GetMapping("/chat")
    public String chat(@RequestParam("prompt") String prompt) {
        try {
            return aiChat.chat("default", prompt);
        } catch (Exception e) {
            log.error("AI 对话异常: {}", e.getMessage(), e);
            return "系统错误: " + e.getMessage();
        }
    }
}
