package com.adroitandroid.model;

import java.io.Serializable;

/**
 * Created by pv on 29/10/16.
 */
public class UserLoginInfo implements Serializable {

    private String userId;
    private String authenticationType;
    private String accessToken;

    public UserLoginInfo() {
    }

    public UserLoginInfo(String authType, String authUserId, String accessToken) {
        this.userId = authUserId;
        this.authenticationType = authType;
        this.accessToken = accessToken;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAuthenticationType() {
        return authenticationType;
    }

    public void setAuthenticationType(String authenticationType) {
        this.authenticationType = authenticationType;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
