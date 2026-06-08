package com.cloudbrainmed.auth.vo;

/**
 * 登录响应VO
 */
public class AuthLoginVo {

    private String patientId;
    private String name;
    private String phone;
    private String token;           // JWT token
    private Long expireTime;        // token过期时间

    public AuthLoginVo() {}

    public AuthLoginVo(String patientId, String name, String phone, String token, Long expireTime) {
        this.patientId = patientId;
        this.name = name;
        this.phone = phone;
        this.token = token;
        this.expireTime = expireTime;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Long expireTime) {
        this.expireTime = expireTime;
    }
}