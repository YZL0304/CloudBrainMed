package com.cloudbrainmed.patient.controller;


import com.cloudbrainmed.patient.service.PatientService;
import com.cloudbrainmed.patient.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 患者病历控制器
 */
@RestController
@RequestMapping("/api/patient/medical-record")
public class MedicalRecordController {

    @Autowired
    private PatientService patientService;

    /**
     * 获取患者的病历列表
     */
    @GetMapping("/list/{patientId}")
    public Result<List<Map<String, Object>>> getMedicalRecords(@PathVariable String patientId) {
        List<Map<String, Object>> records = patientService.getMedicalRecords(patientId);
        return Result.success(records);
    }

    /**
     * 获取病历详情
     */
    @GetMapping("/detail/{recordId}")
    public Result<Map<String, Object>> getMedicalRecordDetail(@PathVariable String recordId,
                                                              @RequestParam String patientId) {
        Map<String, Object> detail = patientService.getMedicalRecordDetail(recordId, patientId);
        if (detail == null) {
            return Result.error("病历不存在");
        }
        return Result.success(detail);
    }
}