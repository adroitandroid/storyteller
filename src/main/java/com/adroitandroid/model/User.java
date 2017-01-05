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

    public static final String AUTH_TYPE = "auth_type";

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

    private Long id;

    @Access(AccessType.FIELD)
    public String username;

    @JoinColumn(name = "auth_type_id")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @OptionalInGson(exclude = AUTH_TYPE)
    @Access(AccessType.FIELD)
    private AuthenticationType authType;

    @Column(name = "auth_user_id")
    @Access(AccessType.FIELD)
    private String authUserId;

    @Column(name = "last_active")
    @Access(AccessType.FIELD)
    private Timestamp lastActiveAt;

    @Column(name = "created_at")
    @Access(AccessType.FIELD)
    private Timestamp createdAt;

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
}
