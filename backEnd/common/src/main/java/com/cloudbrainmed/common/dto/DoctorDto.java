package com.cloudbrainmed.common.dto;


import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DoctorDto {
    private String doctorId;
    private String name;
    private Integer gender;
    private String phone;
    private String email;
    private String position;
    private String goodAt;
    private String introduction;
    private String departmentId;
    private Integer status;
    private LocalDateTime createTime;
}