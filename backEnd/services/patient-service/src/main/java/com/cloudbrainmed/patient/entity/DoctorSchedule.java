package com.cloudbrainmed.patient.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("doctor_schedule")
public class DoctorSchedule {
    @TableId(type = IdType.ASSIGN_ID)
    private String scheduleId;
    private String doctorId;
    private String doctorName;
    private String timeJson;
    private Integer maxNum;
    private Integer remainNum;
    private Integer status;
    private BigDecimal price;
    private String room;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
