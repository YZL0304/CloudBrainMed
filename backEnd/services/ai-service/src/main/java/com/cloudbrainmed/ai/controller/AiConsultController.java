package com.cloudbrainmed.ai.controller;

import com.cloudbrainmed.ai.service.AiConsultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai/consult")
public class AiConsultController {

    @Autowired
    private AiConsultService aiConsultService;

    /**
     * 接诊 AI 分析（供 doctor-service 通过 Feign 调用）
     * 入参: { chiefComplaint, recordDesc, patientAge, patientGender }
     * 返回: { diagnosis, exams, advice, risk }
     */
    @PostMapping("/analyze")
    public Map<String, Object> analyze(@RequestBody Map<String, String> request) {
        return aiConsultService.analyze(request);
    }
}
