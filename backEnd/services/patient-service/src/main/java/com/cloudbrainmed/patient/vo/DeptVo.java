package com.cloudbrainmed.patient.vo;

import lombok.Data;

@Data
public class DeptVo {
    private String deptId;
    private String deptName;
    private String roomId;
    private Integer maxCapacity;
    private Integer freeCapacity;
    private Integer doctorCount;
}