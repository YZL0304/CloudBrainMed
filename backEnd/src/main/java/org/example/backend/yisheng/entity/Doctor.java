package org.example.backend.yisheng.entity;

import java.time.LocalDateTime;

public class Doctor {
    private String doctorId;
    private String avatar;
    private String name;
    private Integer gender;
    private String phone;
    private String email;
    private String position;
    private String goodAt;
    private String introduction;
    private String password;
    private String departmentId;
    private String deptName;
    private Integer status;
    private LocalDateTime createTime;
    private Integer isDeleted;

    // 计算字段：资料是否需完善
    private Boolean needSetup;

    public String getDoctorId() { return doctorId; }
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getGender() { return gender; }
    public void setGender(Integer gender) { this.gender = gender; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
    public String getGoodAt() { return goodAt; }
    public void setGoodAt(String goodAt) { this.goodAt = goodAt; }
    public String getIntroduction() { return introduction; }
    public void setIntroduction(String introduction) { this.introduction = introduction; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getDepartmentId() { return departmentId; }
    public void setDepartmentId(String departmentId) { this.departmentId = departmentId; }
    public String getDeptName() { return deptName; }
    public void setDeptName(String deptName) { this.deptName = deptName; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public Integer getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Integer isDeleted) { this.isDeleted = isDeleted; }
    public Boolean getNeedSetup() { return needSetup; }
    public void setNeedSetup(Boolean needSetup) { this.needSetup = needSetup; }
}
