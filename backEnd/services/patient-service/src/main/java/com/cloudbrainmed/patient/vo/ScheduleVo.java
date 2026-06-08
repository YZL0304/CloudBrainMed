package com.cloudbrainmed.patient.vo;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ScheduleVo {
    private String scheduleId;
    private String timeJson;
    private Integer maxNum;
    private Integer remainNum;
    private BigDecimal price;
    private String room;
}
