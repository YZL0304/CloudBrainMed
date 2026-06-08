package com.cloudbrainmed.auth.vo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

/**
 * 患者信息VO
 */
public class PatientInfoVo {

    private String patientId;

    private String name;

    private String genderText;      // 性别文字描述（男/女/未知）

    private String phone;

    private String address;

    private Integer age;            // 年龄

    private String birthday;        // 生日（字符串格式）

    private LocalDateTime createTime;  // 注册时间

    // 无参构造方法
    public PatientInfoVo() {
    }

    // 全参构造方法
    public PatientInfoVo(String patientId, String name, String genderText,
                         String phone, String address, Integer age,
                         String birthday, LocalDateTime createTime) {
        this.patientId = patientId;
        this.name = name;
        this.genderText = genderText;
        this.phone = phone;
        this.address = address;
        this.age = age;
        this.birthday = birthday;
        this.createTime = createTime;
    }

    // Getters and Setters
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

    public String getGenderText() {
        return genderText;
    }

    public void setGenderText(String genderText) {
        this.genderText = genderText;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    /**
     * 根据生日计算年龄并设置生日字符串
     * @param birthDate 出生日期
     */
    public void calculateAge(LocalDate birthDate) {
        if (birthDate != null) {
            this.age = Period.between(birthDate, LocalDate.now()).getYears();
            this.birthday = birthDate.toString();
        }
    }

    /**
     * 根据性别数字设置性别文字
     * @param gender 性别（1:男, 2:女, 其他:未知）
     */
    public void setGenderByCode(Integer gender) {
        if (gender != null) {
            if (gender == 1) {
                this.genderText = "男";
            } else if (gender == 2) {
                this.genderText = "女";
            } else {
                this.genderText = "未知";
            }
        } else {
            this.genderText = "未知";
        }
    }

    @Override
    public String toString() {
        return "PatientInfoVo{" +
                "patientId='" + patientId + '\'' +
                ", name='" + name + '\'' +
                ", genderText='" + genderText + '\'' +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                ", age=" + age +
                ", birthday='" + birthday + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}