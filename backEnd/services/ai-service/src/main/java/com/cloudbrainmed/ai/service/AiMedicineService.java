package com.cloudbrainmed.ai.service;

import com.cloudbrainmed.ai.dto.MedicineQueryDto;
import com.cloudbrainmed.ai.vo.MedicineAnswerVo;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface AiMedicineService {
    void chatStream(MedicineQueryDto dto, SseEmitter emitter);

    MedicineAnswerVo chat(MedicineQueryDto dto);
}