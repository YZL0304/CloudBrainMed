package com.cloudbrainmed.common.utils;  // 注意包名是 cloudbrainmed，不是 cloudbrained

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 */
@Component  // 确保有这个注解
public class JWTUtil {

    @Value("${jwt.secret:cloudbrainmedSecretKey2024SecureLongKeyForJWT}")
    private String secret;

    @Value("${jwt.expiration:86400000}")
    private Long expiration;

    private Key getSigningKey() {
        byte[] keyBytes = secret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String patientId, String phone) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("patientId", patientId);
        claims.put("phone", phone);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(patientId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Long getExpirationTime() {
        return expiration;
    }
}