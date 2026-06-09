package com.cloudbrainmed.doctor.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("department")
public class Dept {
    @TableId(type = IdType.ASSIGN_ID)
    private String deptId;
    private String deptName;
    private String roomId;
    private Integer maxCapacity;
    private Integer freeCapacity;
    private Integer status;
    private LocalDateTime createTime;
}
