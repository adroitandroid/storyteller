package com.adroitandroid.model;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by pv on 01/12/16.
 */
@Entity
@Table(name = "user")
public class User {

    public User() {
//        Empty constructor as required by hibernate
    }

    public User(Long id) {
        this.id = id;
    }

    private Long id;

    @Access(AccessType.FIELD)
    public String username;

    @Column(name = "auth_type_id")
    @Access(AccessType.FIELD)
    private Integer authTypeId;

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
}
