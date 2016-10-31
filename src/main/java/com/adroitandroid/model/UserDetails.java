package com.adroitandroid.model;

import java.io.Serializable;

/**
 * Created by pv on 29/10/16.
 */
public class UserDetails implements Serializable {
    private final Long userId;
    private final String token;
    private final String username;
    private final Boolean usernameSet;

    public UserDetails(Long userId, String token, String username, Boolean usernameSet) {
        this.userId = userId;
        this.token = token;
        this.username = username;
        this.usernameSet = usernameSet;
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

    public Boolean getUsernameSet() {
        return usernameSet;
    }
}
