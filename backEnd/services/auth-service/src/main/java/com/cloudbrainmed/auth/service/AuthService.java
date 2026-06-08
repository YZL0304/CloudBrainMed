package com.cloudbrainmed.auth.service;


import com.cloudbrainmed.auth.dto.authLoginDto;
import com.cloudbrainmed.auth.dto.authRegisterDto;
import com.cloudbrainmed.auth.vo.AuthLoginVo;
import com.cloudbrainmed.auth.vo.PatientInfoVo;

public interface AuthService {

    /**
     * 患者注册
     */
    PatientInfoVo register(authRegisterDto registerDTO);

    /**
     * 患者登录
     */
    AuthLoginVo login(authLoginDto loginDTO);

    /**
     * 检查手机号是否存在
     */
    boolean checkPhoneExists(String phone);

    /**
     * 检查身份证是否存在
     */
    boolean checkIdCardExists(String idCard);

}