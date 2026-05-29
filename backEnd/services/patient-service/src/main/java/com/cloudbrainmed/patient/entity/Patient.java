package com.cloudbrainmed.patient.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Patient {
    /** 患者ID */
    private String patientId;

    /** 姓名 */
    private String name;

    /** 性别 (0:未知, 1:男, 2:女) */
    private Integer gender;

    /** 手机号 */
    private String phone;

    /** 身份证号 */
    private String idCard;

    /** 地址 */
    private String address;

    /** 密码 */
    private String password;

    /** 出生日期 */
    private LocalDate birthday;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

    /** 是否删除 (0:未删除, 1:已删除) */
    private Integer isDeleted = 0;

    // 无参构造方法
    public Patient() {
    }

    // 全参构造方法
    public Patient(String patientId, String name, Integer gender, String phone,
                   String idCard, String address, String password, LocalDate birthday,
                   LocalDateTime createTime, LocalDateTime updateTime, Integer isDeleted) {
        this.patientId = patientId;
        this.name = name;
        this.gender = gender;
        this.phone = phone;
        this.idCard = idCard;
        this.address = address;
        this.password = password;
        this.birthday = birthday;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.isDeleted = isDeleted;
    }

    // Getter 和 Setter 方法
    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    @Override
    public String toString() {
        return "Patient{" +
                "patientId='" + patientId + '\'' +
                ", name='" + name + '\'' +
                ", gender=" + gender +
                ", phone='" + phone + '\'' +
                ", idCard='" + idCard + '\'' +
                ", address='" + address + '\'' +
                ", password='" + password + '\'' +
                ", birthday=" + birthday +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", isDeleted=" + isDeleted +
                '}';
    }
}