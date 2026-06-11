package com.cloudbrainmed.patient.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 处方实体
 */
public class Prescription {
    private String prescriptionId;
    private String registerId;
    private String patientId;
    private String doctorId;
    private String medicineName;
    private String dosage;
    private String usage_;
    private Integer quantity;
    private BigDecimal price;
    private String payStatus;
    private LocalDateTime createTime;

    public String getPrescriptionId() { return prescriptionId; }
    public void setPrescriptionId(String prescriptionId) { this.prescriptionId = prescriptionId; }
    public String getRegisterId() { return registerId; }
    public void setRegisterId(String registerId) { this.registerId = registerId; }
    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
    public String getDoctorId() { return doctorId; }
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }
    public String getMedicineName() { return medicineName; }
    public void setMedicineName(String medicineName) { this.medicineName = medicineName; }
    public String getDosage() { return dosage; }
    public void setDosage(String dosage) { this.dosage = dosage; }
    public String getUsage_() { return usage_; }
    public void setUsage_(String usage_) { this.usage_ = usage_; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public String getPayStatus() { return payStatus; }
    public void setPayStatus(String payStatus) { this.payStatus = payStatus; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
