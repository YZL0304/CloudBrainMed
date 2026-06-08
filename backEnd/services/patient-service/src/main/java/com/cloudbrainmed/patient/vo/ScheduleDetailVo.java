package com.cloudbrainmed.patient.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ScheduleDetailVo {
    private String scheduleId;
    private String doctorId;
    private String doctorName;
    private String doctorPosition;
    private String goodAt;
    private String departmentName;
    private BigDecimal price;
    private Integer remainNum;
    private String room;
    private List<TimeSlot> timeSlots;
}
