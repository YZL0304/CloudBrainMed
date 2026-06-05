package com.cloudbrainmed.patient.vo;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class DoctorVo {
    private String doctorId;
    private String name;
    private String position;
    private String goodAt;
    private String avatar;
    private String departmentName;
    private BigDecimal price;
    private Integer remainNum;
}
