package com.cloudbrainmed.common.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DeptDto {
    private String deptId;
    private String deptName;
    private String roomId;
    private Integer maxCapacity;
    private Integer freeCapacity;
    private Integer status;
    private LocalDateTime createTime;
}
