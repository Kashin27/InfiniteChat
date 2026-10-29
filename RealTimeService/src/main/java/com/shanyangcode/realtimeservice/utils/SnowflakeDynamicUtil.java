package com.shanyangcode.realtimeservice.utils;


import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 雪花 ID 生成工具类
 *
 * @author shanyang
 */
@Component
public class SnowflakeDynamicUtil {

    @Value("${snowflake.workerId}")
    private long workerId;

    @Value("${snowflake.datacenterId}")
    private long dataCenterId;

    private static volatile Snowflake SNOWFLAKE;

    @PostConstruct
    public void init() {
        SNOWFLAKE = IdUtil.getSnowflake(workerId, dataCenterId);
    }

    /**
     * 生成雪花 ID
     *
     * @return 雪花 ID
     */
    public static long nextId() {
        Snowflake snowflake = SNOWFLAKE;
        if (snowflake == null) {
            throw new IllegalStateException("SnowflakeDynamicUtil 尚未初始化完成");
        }
        return snowflake.nextId();
    }

}