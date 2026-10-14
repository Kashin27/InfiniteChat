package com.shanyangcode.aiservice.tool;

import dev.langchain4j.agent.tool.Tool;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class TimeTool {
    @Tool("获取上海时间")
    public String getTimeinShanghai() {
        ZonedDateTime now= ZonedDateTime.now(ZoneId.of("Asia/Shanghai"));
        return now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
