package com.cloudbrainmed.ai.vo;

import lombok.Data;

@Data
public class MedicineAnswerVo {
    private String answer;
    private String medicineName;
    private String usage;
    private String indication;
    private String attention;
}