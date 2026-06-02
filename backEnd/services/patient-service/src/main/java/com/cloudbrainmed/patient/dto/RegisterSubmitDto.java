package com.cloudbrainmed.patient.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 提交挂号DTO
 */
public class RegisterSubmitDto {

    @NotBlank(message = "患者ID不能为空")
    private String patientId;

    @NotBlank(message = "科室ID不能为空")
    private String deptId;

    @NotBlank(message = "医生ID不能为空")
    private String doctorId;

    @NotBlank(message = "挂号日期不能为空")
    private String registerDate;

    @NotBlank(message = "挂号时间段不能为空")
    private String timeSlot;

    @NotNull(message = "挂号费用不能为空")
    private Double fee;

    private String symptom;

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public String getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(String registerDate) {
        this.registerDate = registerDate;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }

    public Double getFee() {
        return fee;
    }

    public void setFee(Double fee) {
        this.fee = fee;
    }

    public String getSymptom() {
        return symptom;
    }

    public void setSymptom(String symptom) {
        this.symptom = symptom;
    }
}