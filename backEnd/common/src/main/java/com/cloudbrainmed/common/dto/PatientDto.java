package com.cloudbrainmed.common.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PatientDto {
    private String patientId;
    private String name;
    private Integer gender;
    private String phone;
    private String idCard;
    private String address;
    private LocalDate birthday;
    private LocalDateTime createTime;
}