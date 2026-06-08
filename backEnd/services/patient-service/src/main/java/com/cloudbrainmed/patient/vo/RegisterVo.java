package com.cloudbrainmed.patient.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class RegisterVo {
    private String registerId;
    private String doctorId;
    private String doctorName;
    private String doctorPosition;
    private String department;
    private LocalDate visitDate;
    private String consultTime;
    private BigDecimal price;
    private String payStatus;
}