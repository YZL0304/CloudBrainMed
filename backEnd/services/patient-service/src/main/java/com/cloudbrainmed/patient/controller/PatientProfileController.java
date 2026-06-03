package com.cloudbrainmed.patient.controller;


import com.cloudbrainmed.patient.dto.ChangePasswordDto;
import com.cloudbrainmed.patient.dto.PatientInfoDto;
import com.cloudbrainmed.patient.dto.authRegisterDto;
import com.cloudbrainmed.patient.entity.Patient;
import com.cloudbrainmed.patient.service.PatientService;
import com.cloudbrainmed.patient.vo.PatientInfoVo;
import com.cloudbrainmed.patient.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * 患者个人信息控制器
 */
@RestController
@RequestMapping("/api/patient")
@Validated
public class PatientProfileController {

    @Autowired
    private PatientService patientService;

    /**
     * 患者移动端注册
     */
    @PostMapping("/auth/register")
    public Result<PatientInfoVo> register(@Valid @RequestBody authRegisterDto registerDTO) {
        // 1. 检查手机号是否已存在
        if (patientService.checkPhoneExists(registerDTO.getPhone())) {
            return Result.error(400, "手机号已注册");
        }

        // 2. 检查身份证是否已存在
        if (patientService.checkIdCardExists(registerDTO.getIdCard())) {
            return Result.error(400, "身份证已注册");
        }

        // 3. 执行注册
        PatientInfoVo patientInfo = patientService.register(registerDTO);
        if (patientInfo == null) {
            return Result.error(500, "注册失败，请稍后重试");
        }

        return Result.success("注册成功", patientInfo);
    }


    /**
     * 获取患者信息
     */
    @GetMapping("/info/{patientId}")
    public Result<Patient> getPatientInfo(@PathVariable String patientId) {
        Patient patientInfo = patientService.getPatientInfo(patientId);
        if (patientInfo == null) {
            return Result.error("患者不存在");
        }
        return Result.success(patientInfo);
    }

    /**
     * 更新患者信息
     */
    @PutMapping("/update")
    public Result<String> updatePatientInfo(@Valid @RequestBody PatientInfoDto patientInfoDto) {
        boolean result = patientService.updatePatientInfo(patientInfoDto);
        if (result) {
            return Result.success("更新成功", null);
        }
        return Result.error("更新失败");
    }

    /**
     * 修改密码
     */
    @PostMapping("/change-password")
    public Result<String> changePassword(@Valid @RequestBody ChangePasswordDto changePasswordDto) {
        // 验证新密码和确认密码是否一致
        if (!changePasswordDto.getNewPassword().equals(changePasswordDto.getConfirmPassword())) {
            return Result.error("新密码和确认密码不一致");
        }

        boolean result = patientService.changePassword(changePasswordDto);
        if (result) {
            return Result.success("密码修改成功，请重新登录", null);
        }
        return Result.error("密码修改失败，请检查原密码是否正确");
    }
}