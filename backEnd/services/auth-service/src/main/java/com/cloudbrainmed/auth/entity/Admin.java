package com.cloudbrainmed.auth.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Admin {
    // 管理员ID
    private String adminId;
    // 头像
    private String avatar;
    // 姓名
    private String name;
    // 性别
    private Integer gender;
    // 电话
    private String phone;
    // 邮箱
    private String email;
    // 职位
    private String position;
    // 密码
    private String password;
    // 0停用 1正常
    private Integer status;
    // 创建时间
    private Date createTime;
    // 0未删除 1已删除
    private Integer isDeleted;
}
