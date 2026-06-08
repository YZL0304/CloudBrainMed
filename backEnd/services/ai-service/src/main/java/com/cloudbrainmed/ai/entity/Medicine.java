package com.cloudbrainmed.ai.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("medicine")
public class Medicine {
    @TableId("medicine_id")
    private String medicineId;

    private String name;
    private String spec;
    private String usage;
    private String indication;
    private String attention;
    private Integer stock;
    private BigDecimal price;
    private LocalDateTime createTime;
}