package com.adroitandroid.model;

import java.io.Serializable;

/**
 * Created by pv on 02/12/16.
 */
public class UserLoginInfo implements Serializable {

    private String authUserId;
    private String authenticationType;
    private String accessToken;

    public UserLoginInfo() {
    }

    public UserLoginInfo(String authType, String authUserId, String accessToken) {
        this.authUserId = authUserId;
        this.authenticationType = authType;
        this.accessToken = accessToken;
    }

    public String getAuthUserId() {
        return authUserId;
    }

    public void setAuthUserId(String authUserId) {
        this.authUserId = authUserId;
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
