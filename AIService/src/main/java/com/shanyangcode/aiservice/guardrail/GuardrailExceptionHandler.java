package com.shanyangcode.aiservice.guardrail;

import com.shanyangcode.common.exception.ErrorCode;
import com.shanyangcode.common.response.BaseResponse;
import com.shanyangcode.common.response.ResultUtils;
import dev.langchain4j.guardrail.InputGuardrailException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GuardrailExceptionHandler {
    @ExceptionHandler(InputGuardrailException.class)
    public BaseResponse<?> inputGuardrailExceptionHandler(InputGuardrailException e) {
        log.error("敏感词拦截: {}", e.getMessage());
        return ResultUtils.error(ErrorCode.SENSITIVE_WORD_ERROR);
    }
}