package com.adroitandroid.model;

import java.io.Serializable;

/**
 * Created by pv on 02/12/16.
 */
public class UserLoginDetails implements Serializable {
    private final Long userId;
    private final String token;
    public final String username;

    public UserLoginDetails(Long userId, String token, String username) {
        this.userId = userId;
        this.token = token;
        this.username = username;
    }

    public Long getUserId() {
        return userId;
    }

    public String getToken() {
        return token;
    }

    public String getUsername() {
        return username;
    }
}
