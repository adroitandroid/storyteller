package com.adroitandroid.model;

import java.io.Serializable;

/**
 * Created by pv on 02/12/16.
 */
public class UserDetails implements Serializable {
    private final Long userId;
    private final String token;
    private final String username;

    public UserDetails(Long userId, String token, String username) {
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
