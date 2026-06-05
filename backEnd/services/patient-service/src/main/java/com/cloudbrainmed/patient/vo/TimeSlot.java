package com.cloudbrainmed.patient.vo;

import lombok.Data;

@Data
public class TimeSlot {
    private String time;
    private String status;
    private Integer remainNum;
}
