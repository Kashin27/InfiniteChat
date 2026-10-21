package com.shanyangcode.aiservice.ai;

import com.shanyangcode.aiservice.model.dto.ChatRequest;
import com.shanyangcode.aiservice.monitor.MonitorContext;
import com.shanyangcode.aiservice.monitor.MonitorContextHolder;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@Slf4j
@RestController
public class AiChatController {

    @Resource
    private AiChat aiChat;

    @PostMapping("/chat")
    public String chat(@RequestBody ChatRequest chatRequest) {
        MonitorContextHolder.setContext(MonitorContext.builder().userId(chatRequest.getUserId()).sessionId(chatRequest.getSessionId()).build());
        String chat = aiChat.chat(chatRequest.getSessionId(), chatRequest.getPrompt());
        MonitorContextHolder.clearContext();
        return chat;
    }


    @PostMapping(value = "/streamChat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamChat(@RequestBody ChatRequest chatRequest) {
        MonitorContext context = MonitorContext.builder()
                .userId(chatRequest.getUserId())
                .sessionId(chatRequest.getSessionId())
                .build();

        return Flux.defer(() -> {
            MonitorContextHolder.setContext(context);
            return aiChat.streamChat(chatRequest.getSessionId(), chatRequest.getPrompt())
                    .doFinally(signal -> MonitorContextHolder.clearContext());
        });
    }
}
