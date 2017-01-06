package com.adroitandroid.model;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by pv on 06/01/17.
 */
@Entity
@Table(name = "user_bookmarks")
public class UserBookmark implements Serializable {
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    private Snippet snippet;

    @Column(name = "soft_deleted")
    private boolean softDeleted;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @ManyToOne
    @JoinColumn(name = "snippet_id")
    public Snippet getSnippet() {
        return snippet;
    }

    public void setSnippet(Snippet snippet) {
        this.snippet = snippet;
    }

    public boolean getSoftDeleted() {
        return softDeleted;
    }

    public void setSoftDeleted(boolean softDeleted) {
        this.softDeleted = softDeleted;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
}
