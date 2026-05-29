package com.cloudbrainmed.patient.controller;


import com.cloudbrainmed.patient.dto.RegisterQueryDto;
import com.cloudbrainmed.patient.dto.RegisterSubmitDto;
import com.cloudbrainmed.patient.service.PatientService;
import com.cloudbrainmed.patient.vo.RegisterVo;
import com.cloudbrainmed.patient.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 患者挂号控制器
 */
@RestController
@RequestMapping("/api/patient/register")
@Validated
public class RegisterController {

    @Autowired
    private PatientService patientService;

    /**
     * 获取可预约科室列表
     */
    @GetMapping("/depts")
    public Result<List<Map<String, Object>>> getAvailableDepts() {
        List<Map<String, Object>> depts = patientService.getAvailableDepts();
        return Result.success(depts);
    }

    /**
     * 根据科室获取医生列表
     */
    @GetMapping("/doctors")
    public Result<List<Map<String, Object>>> getDoctorsByDept(@RequestParam String deptId) {
        List<Map<String, Object>> doctors = patientService.getDoctorsByDept(deptId);
        return Result.success(doctors);
    }

    /**
     * 获取医生的可预约时间段
     */
    @GetMapping("/timeslots")
    public Result<List<String>> getAvailableTimeSlots(@RequestParam String doctorId,
                                                      @RequestParam String registerDate) {
        List<String> timeSlots = patientService.getAvailableTimeSlots(doctorId, registerDate);
        return Result.success(timeSlots);
    }

    /**
     * 提交挂号
     */
    @PostMapping("/submit")
    public Result<String> submitRegister(@Valid @RequestBody RegisterSubmitDto registerSubmitDto) {
        boolean result = patientService.submitRegister(registerSubmitDto);
        if (result) {
            return Result.success("挂号成功", null);
        }
        return Result.error("挂号失败");
    }

    /**
     * 查询挂号记录
     */
    @PostMapping("/history")
    public Result<List<RegisterVo>> getRegisterHistory(@RequestBody RegisterQueryDto registerQueryDto) {
        List<RegisterVo> registers = patientService.getRegisterHistory(registerQueryDto);
        return Result.success(registers);
    }

    /**
     * 取消挂号
     */
    @PutMapping("/cancel/{registerId}")
    public Result<String> cancelRegister(@PathVariable String registerId, @RequestParam String patientId) {
        boolean result = patientService.cancelRegister(registerId, patientId);
        if (result) {
            return Result.success("取消挂号成功", null);
        }
        return Result.error("取消挂号失败");
    }

    /**
     * 获取挂号详情
     */
    @GetMapping("/detail/{registerId}")
    public Result<RegisterVo> getRegisterDetail(@PathVariable String registerId, @RequestParam String patientId) {
        RegisterVo detail = patientService.getRegisterDetail(registerId, patientId);
        if (detail == null) {
            return Result.error("挂号记录不存在");
        }
        return Result.success(detail);
    }
}