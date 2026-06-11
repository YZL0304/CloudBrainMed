package com.cloudbrainmed.api.feign;

import com.cloudbrainmed.api.fallback.AiFeignFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "ai-service", fallback = AiFeignFallback.class)
public interface AiFeignClient {

    /**
     * 接诊 AI 分析
     * @param request 包含 registerId, chiefComplaint, recordDesc, patientAge, patientGender
     * @return AI 分析结果 { diagnosis, exams, advice, risk }
     */
    @PostMapping("/api/ai/consult/analyze")
    Map<String, Object> analyzeConsult(@RequestBody Map<String, String> request);
}
