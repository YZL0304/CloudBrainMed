package com.cloudbrainmed.auth.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Patient {
    // 患者ID
    private String patientId;
    // 姓名
    private String name;
    // 性别 1男 2女
    private Integer gender;
    // 电话
    private String phone;
    // 身份证号
    private String idCard;
    // 住址
    private String address;
    // 密码（加密）
    private String password;
    // 出生日期
    private Date birthday;
    // 创建时间
    private Date createTime;
    // 更新时间
    private Date updateTime;
    // 0未删除 1已删除
    private Integer isDeleted;
}
