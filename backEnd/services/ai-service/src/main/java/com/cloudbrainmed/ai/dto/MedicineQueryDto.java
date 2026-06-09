package com.cloudbrainmed.ai.dto;

import lombok.Data;

@Data
public class MedicineQueryDto {
    private String sessionId;
    private String question;
    private String medicineId;
}