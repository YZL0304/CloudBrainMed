package com.cloudbrainmed.common.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 */
@Component
public class JWTUtil_temp {

    @Value("${jwt.secret:cloudbrainmedSecretKey2024SecureLongKeyForJWT}")
    private String secret;

    @Value("${jwt.expiration:86400000}")
    private Long expiration;

    /**
     * 获取签名密钥
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 生成Token
     */
    public String generateToken(String patientId, String phone) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("patientId", patientId);
        claims.put("phone", phone);
        claims.put("role", "PATIENT");

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(patientId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 从Token中获取Claims
     */
    public Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 获取患者ID
     */
    public String getPatientIdFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    /**
     * 获取手机号
     */
    public String getPhoneFromToken(String token) {
        return (String) getClaimsFromToken(token).get("phone");
    }

    /**
     * 获取角色
     */
    public String getRoleFromToken(String token) {
        return (String) getClaimsFromToken(token).get("role");
    }

    /**
     * 验证Token是否有效
     */
    public Boolean validateToken(String token) {
        try {
            getClaimsFromToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 判断Token是否过期
     */
    public Boolean isTokenExpired(String token) {
        try {
            Date expiration = getClaimsFromToken(token).getExpiration();
            return expiration.before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    public Long getExpirationTime() {
        return expiration;
    }
}