package com.cloudbrainmed.auth.controller;

import com.cloudbrainmed.auth.dto.authLoginDto;
import com.cloudbrainmed.auth.dto.authRegisterDto;
import com.cloudbrainmed.auth.service.AuthService;
import com.cloudbrainmed.auth.vo.AuthLoginVo;
import com.cloudbrainmed.auth.vo.PatientInfoVo;
import com.cloudbrainmed.common.result.Result;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth-service")
@Validated
public class PatientAuthController {

    @Autowired
    private AuthService authService;

    /**
     * 患者移动端注册
     */
    @PostMapping("/patient/register")
    public Result<PatientInfoVo> register(@Valid @RequestBody authRegisterDto registerDTO) {
        // 1. 检查手机号是否已存在
        if (authService.checkPhoneExists(registerDTO.getPhone())) {
            return Result.error(400, "手机号已注册");
        }

        // 2. 检查身份证是否已存在
        if (authService.checkIdCardExists(registerDTO.getIdCard())) {
            return Result.error(400, "身份证已注册");
        }

        // 3. 执行注册
        PatientInfoVo patientInfo = authService.register(registerDTO);
        if (patientInfo == null) {
            return Result.error(500, "注册失败，请稍后重试");
        }

        return Result.success("注册成功", patientInfo);
    }

    /**
     * 患者移动端登录
     */
    @PostMapping("/patient/login")
    public Result<AuthLoginVo> login(@Valid @RequestBody authLoginDto loginDTO) {
        // 1. 执行登录
        AuthLoginVo loginResult = authService.login(loginDTO);

        // 2. 检查登录结果
        if (loginResult == null) {
            return Result.error(401, "手机号或密码错误");
        }

        // 3. 返回登录成功信息
        return Result.success("登录成功", loginResult);
    }
}