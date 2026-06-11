package com.cloudbrainmed.api.fallback;

import com.cloudbrainmed.api.feign.AiFeignClient;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class AiFeignFallback implements AiFeignClient {

    @Override
    public Map<String, Object> analyzeConsult(Map<String, String> request) {
        Map<String, Object> fallback = new HashMap<>();
        fallback.put("diagnosis", "AI 服务暂不可用，请稍后重试");
        fallback.put("exams", java.util.Collections.emptyList());
        fallback.put("advice", "请基于临床经验做出判断");
        fallback.put("risk", "无法获取 AI 风险评估");
        return fallback;
    }
}
