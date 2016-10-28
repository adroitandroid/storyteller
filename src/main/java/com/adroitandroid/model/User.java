package com.adroitandroid.model;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by pv on 27/10/16.
 */
@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String username;

    @Column(name = "auth_type_id")
    private Integer authenticationType;

    @Column(name = "auth_user_id")
    private String authenticationId;

    @Column(name = "last_login")
    private Timestamp lastLogin;

    public User() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getAuthenticationType() {
        return authenticationType;
    }

    public void setAuthenticationType(Integer authenticationType) {
        this.authenticationType = authenticationType;
    }

    public String getAuthenticationId() {
        return authenticationId;
    }

    public void setAuthenticationId(String authenticationId) {
        this.authenticationId = authenticationId;
    }

    public Timestamp getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Timestamp lastLogin) {
        this.lastLogin = lastLogin;
    }
}
