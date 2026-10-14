package com.shanyangcode.aiservice.ai;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.guardrail.InputGuardrails;
import com.shanyangcode.aiservice.guardrail.SafeInputGuardrail;

@InputGuardrails({SafeInputGuardrail.class})
public interface AiChat {
    @SystemMessage(fromResource = "system prompt/chat-box.txt")
    String chat(@MemoryId String memoryId, @UserMessage String prompt);
}