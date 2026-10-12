package com.shanyangcode.aiservice.ai;

import dev.langchain4j.service.SystemMessage;

public interface AiChat {
    @SystemMessage(fromResource = "system prompt/chat-box.txt")
    String chat(String prompt);
}