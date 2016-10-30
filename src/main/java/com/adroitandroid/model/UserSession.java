package com.adroitandroid.model;

import org.springframework.data.annotation.Id;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;

/**
 * Created by pv on 29/10/16.
 */
public class UserSession {
    @Id
    private String id;
    private Long userId;
    private String authType;
    private String authUserId;
    private String accessToken;
    private String sessionId;
    private Date expiryTime;

    public UserSession(Long userId, String authType, String authUserId, String accessToken, Date expiryTime)
            throws NoSuchAlgorithmException {
        this.userId = userId;
        this.authType = authType;
        this.authUserId = authUserId;
        this.accessToken = accessToken;
        this.expiryTime = expiryTime;
        this.sessionId = generateNewSessionId();
    }

//    TODO: explore how JWT generates unique access token
    private String generateNewSessionId() throws NoSuchAlgorithmException {
        final byte[] sessionInBytes = new byte[256];
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        sr.nextBytes(sessionInBytes);
        return Base64.getEncoder().encodeToString(sessionInBytes);
    }

    public String getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getAuthType() {
        return authType;
    }

    public void setAuthType(String authType) {
        this.authType = authType;
    }

    public String getAuthUserId() {
        return authUserId;
    }

    public void setAuthUserId(String authUserId) {
        this.authUserId = authUserId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Date getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(Date expiryTime) {
        this.expiryTime = expiryTime;
    }
}
