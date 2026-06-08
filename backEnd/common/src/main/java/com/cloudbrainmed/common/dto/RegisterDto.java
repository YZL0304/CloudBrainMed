package com.cloudbrainmed.common.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class RegisterDto {
    private String registerId;
    private String patientId;
    private String doctorId;
    private String name;
    private Integer gender;
    private LocalDate birthday;
    private String chiefComplaint;
    private String department;
    private String consultRoom;
    private LocalDate visitDate;
    private String consultTime;
    private BigDecimal price;
    private String payStatus;
    private LocalDateTime createTime;
}