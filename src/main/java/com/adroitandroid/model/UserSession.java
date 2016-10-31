package com.adroitandroid.model;

import com.adroitandroid.model.service.UserService;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;

/**
 * Created by pv on 29/10/16.
 */
@Document(collection = "userSessions")
public class UserSession implements Serializable {
    @Id
    private String id;
    private Long userId;
    private String authType;
    private String authUserId;
    private String accessToken;
    private String sessionId;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Indexed(expireAfterSeconds = UserService.SESSION_EXPIRY_IN_SEC)
    private Date creationTime;

    public UserSession(Long userId, String authType, String authUserId, String accessToken, Date creationTime)
            throws NoSuchAlgorithmException {
        this.userId = userId;
        this.authType = authType;
        this.authUserId = authUserId;
        this.accessToken = accessToken;
        this.creationTime = creationTime;
        this.sessionId = generateNewSessionId();
    }

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

    public String getAuthType() {
        return authType;
    }

    public String getAuthUserId() {
        return authUserId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getSessionId() {
        return sessionId;
    }

    public Date getCreationTime() {
        return creationTime;
    }
}
