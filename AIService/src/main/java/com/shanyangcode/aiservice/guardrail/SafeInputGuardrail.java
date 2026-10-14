package com.shanyangcode.aiservice.guardrail;

import dev.langchain4j.guardrail.GuardrailRequestParams;
import dev.langchain4j.guardrail.InputGuardrail;
import dev.langchain4j.guardrail.InputGuardrailRequest;
import dev.langchain4j.guardrail.InputGuardrailResult;
import dev.langchain4j.model.input.PromptTemplate;

import java.util.Map;
import java.util.Set;

public class SafeInputGuardrail implements InputGuardrail {

    private static final Set<String> SENSITIVE_WORDS = Set.of("死", "杀");

    @Override
    public InputGuardrailResult validate(InputGuardrailRequest request){
        String rawInput=extractRawUserInput(request);

        for(String keyword:SENSITIVE_WORDS){
            if(!keyword.isEmpty()&&rawInput.contains(keyword)){
                return fatal("输入包含敏感内容");
            }
        }
        return success();

    }
    private String extractRawUserInput(InputGuardrailRequest request) {
        GuardrailRequestParams params = request.requestParams();
        String template= params.userMessageTemplate();
        if(template==null){
            return request.userMessage().singleText();
        }
        Map<String, Object> vars=params.variables()==null? Map.of():params.variables();
        return PromptTemplate.from(template).apply(vars).text();

    }
}
