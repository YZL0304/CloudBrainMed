package com.cloudbrainmed.doctor.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 医生排班实体
 */
public class Schedule {
    private String scheduleId;
    private String doctorId;
    private String deptId;
    private LocalDate workDate;
    private String timeSlot;
    private Integer maxNum;
    private Integer remainNum;
    private BigDecimal price;
    private String status;
    private LocalDateTime createTime;

    public String getScheduleId() { return scheduleId; }
    public void setScheduleId(String scheduleId) { this.scheduleId = scheduleId; }
    public String getDoctorId() { return doctorId; }
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }
    public String getDeptId() { return deptId; }
    public void setDeptId(String deptId) { this.deptId = deptId; }
    public LocalDate getWorkDate() { return workDate; }
    public void setWorkDate(LocalDate workDate) { this.workDate = workDate; }
    public String getTimeSlot() { return timeSlot; }
    public void setTimeSlot(String timeSlot) { this.timeSlot = timeSlot; }
    public Integer getMaxNum() { return maxNum; }
    public void setMaxNum(Integer maxNum) { this.maxNum = maxNum; }
    public Integer getRemainNum() { return remainNum; }
    public void setRemainNum(Integer remainNum) { this.remainNum = remainNum; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
