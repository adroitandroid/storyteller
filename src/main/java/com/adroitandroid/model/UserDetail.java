package com.adroitandroid.model;

import com.adroitandroid.OptionalInGson;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by pv on 07/12/16.
 */
@Entity
@Table(name = "user_details")
public class UserDetail {
    public static final String USER = "user_in_user_details";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    @OptionalInGson(exclude = USER)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_user_id")
    private User user;

    @Column(name = "fcm_token")
    private String fcmToken;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "soft_deleted")
    private Boolean softDeleted;
}
