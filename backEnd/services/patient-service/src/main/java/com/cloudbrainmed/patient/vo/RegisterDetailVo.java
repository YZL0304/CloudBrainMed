package com.cloudbrainmed.patient.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class RegisterDetailVo {
    private String registerId;
    private String patientName;
    private Integer patientGender;
    private LocalDate patientBirthday;
    private String patientPhone;
    private String doctorName;
    private String doctorPosition;
    private String department;
    private String consultRoom;
    private LocalDate visitDate;
    private String consultTime;
    private String chiefComplaint;
    private BigDecimal price;
    private String payStatus;
    private LocalDateTime createTime;
}
