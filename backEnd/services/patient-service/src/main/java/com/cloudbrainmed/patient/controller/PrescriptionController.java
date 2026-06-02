package com.cloudbrainmed.patient.controller;

import com.cloudbrainmed.patient.service.PatientService;
import com.cloudbrainmed.patient.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 患者处方控制器
 */
@RestController
@RequestMapping("/api/patient/prescription")
public class PrescriptionController {

    @Autowired
    private PatientService patientService;

    /**
     * 获取患者的处方列表
     */
    @GetMapping("/list/{patientId}")
    public Result<List<Map<String, Object>>> getPrescriptions(@PathVariable String patientId) {
        List<Map<String, Object>> prescriptions = patientService.getPrescriptions(patientId);
        return Result.success(prescriptions);
    }

    /**
     * 获取处方详情
     */
    @GetMapping("/detail/{prescriptionId}")
    public Result<Map<String, Object>> getPrescriptionDetail(@PathVariable String prescriptionId,
                                                             @RequestParam String patientId) {
        Map<String, Object> detail = patientService.getPrescriptionDetail(prescriptionId, patientId);
        if (detail == null) {
            return Result.error("处方不存在");
        }
        return Result.success(detail);
    }
}