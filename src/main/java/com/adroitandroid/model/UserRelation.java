package com.adroitandroid.model;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by pv on 07/01/17.
 */
@Entity
@Table(name = "user_relations")
public class UserRelation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    private Long followerUserId;

    @Column(name = "soft_deleted")
    private boolean softDeleted;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    public Long getUserId() {
        return userId;
    }

    public Long getFollowerUserId() {
        return followerUserId;
    }

    public boolean isSoftDeleted() {
        return softDeleted;
    }
}
