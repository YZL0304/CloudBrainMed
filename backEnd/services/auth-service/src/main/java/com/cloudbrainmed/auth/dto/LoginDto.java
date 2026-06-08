package com.cloudbrainmed.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginDto {

    private String phone;
    private String password;
    private Integer roleType; // 1患者 2医生 3管理员
}