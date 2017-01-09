package com.adroitandroid.model;

import com.adroitandroid.serializer.OptionalInGson;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by pv on 01/12/16.
 */
@Entity
@Table(name = "user")
public class User {

    public static final String AUTH_TYPE_IN_USER = "auth_type_in_user";
    public static final String USER_STATS_IN_USER = "user_stats_in_user";

    public User() {
//        Empty constructor as required by hibernate
    }

    public User(Long id) {
        this.id = id;
    }

    public User(String authenticationType, String authUserId) {
        this.authType = AuthenticationType.getByType(authenticationType);
        this.authUserId = authUserId;
        setCreatedAndLastActiveTime();
    }

    public User(String authenticationType, String authUserId, String name, String photoUrl, String emailId) {
        this.authType = AuthenticationType.getByType(authenticationType);
        this.authUserId = authUserId;
        this.username = name;
        this.photoUrl = photoUrl;
        this.emailId = emailId;
        setCreatedAndLastActiveTime();
    }

    private Long id;

    @Access(AccessType.FIELD)
    public String username;

    @Access(AccessType.FIELD)
    public String description;

    @JoinColumn(name = "auth_type_id")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @OptionalInGson(exclude = AUTH_TYPE_IN_USER)
    @Access(AccessType.FIELD)
    private AuthenticationType authType;

    @Column(name = "auth_user_id")
    @Access(AccessType.FIELD)
    private String authUserId;

    @Column(name = "photo_url")
    @Access(AccessType.FIELD)
    private String photoUrl;

    @Column(name = "email_id")
    @Access(AccessType.FIELD)
    private String emailId;

    @Column(name = "last_active")
    @Access(AccessType.FIELD)
    private Timestamp lastActiveAt;

    @Column(name = "created_at")
    @Access(AccessType.FIELD)
    private Timestamp createdAt;

    @OneToOne(mappedBy="user", cascade=CascadeType.ALL, fetch = FetchType.LAZY)
    @OptionalInGson(exclude = USER_STATS_IN_USER)
    @Access(value = AccessType.FIELD)
    private UserStats userStats;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setLastActiveAt(Timestamp lastActiveAt) {
        this.lastActiveAt = lastActiveAt;
    }

    public String getUsername() {
        return username;
    }

    private void setCreatedAndLastActiveTime() {
        this.lastActiveAt = new Timestamp((new Date()).getTime());
        this.createdAt = this.lastActiveAt;
    }

    public Timestamp getLastActiveAt() {
        return lastActiveAt;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
