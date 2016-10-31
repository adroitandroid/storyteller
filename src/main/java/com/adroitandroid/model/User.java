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

    @JoinColumn(name = "auth_type_id")
    @ManyToOne(optional = false)
    private AuthenticationType authType;

    @Column(name = "auth_user_id")
    private String authId;

    @Column(name = "last_active")
    private Timestamp lastActive;

    public User() {
    }

    public User(String username, String authType, String authId, Timestamp lastActive) {
        this.username = username;
        this.authType = AuthenticationType.getByType(authType);
        this.authId = authId;
        this.lastActive = lastActive;
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

    public AuthenticationType getAuthType() {
        return authType;
    }

    public String getAuthId() {
        return authId;
    }

    public Timestamp getLastActive() {
        return lastActive;
    }

    public void setLastActive(Timestamp lastActive) {
        this.lastActive = lastActive;
    }
}
