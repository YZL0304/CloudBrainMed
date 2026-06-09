package com.cloudbrainmed.doctor.vo;

import lombok.Data;

@Data
public class DoctorProfileVo {
    private String name;
    private String deptName;
    private String position;
    private String avatar;
    private String goodAt;
    private String introduction;
    private String email;
}
