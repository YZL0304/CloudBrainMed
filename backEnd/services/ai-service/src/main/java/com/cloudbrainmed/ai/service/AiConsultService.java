package com.cloudbrainmed.ai.service;

import java.util.Map;

public interface AiConsultService {
    /**
     * AI 接诊分析
     * @param request 包含 chiefComplaint, recordDesc, patientAge, patientGender
     * @return 结构化分析结果
     */
    Map<String, Object> analyze(Map<String, String> request);
}
