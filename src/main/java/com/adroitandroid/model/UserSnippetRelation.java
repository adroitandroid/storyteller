package com.adroitandroid.model;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by pv on 27/10/16.
 */
@Entity
@Table(name = "user_snippet_relation")
public class UserSnippetRelation implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @JoinColumn(name = "snippet_id")
    @ManyToOne(optional = false)
    private StorySnippet storySnippet;

    @Column(name = "relation_type")
    private String relationType;

    @Column(name = "created_at")
    private Timestamp createTime;

    @Column(name = "updated_at")
    private Timestamp updateTime;

    @Column(name = "soft_deleted")
    private Boolean softDeleted;

    public UserSnippetRelation() {
    }

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

    public StorySnippet getStorySnippet() {
        return storySnippet;
    }

    public void setStorySnippet(StorySnippet storySnippet) {
        this.storySnippet = storySnippet;
    }

    public String getRelationType() {
        return relationType;
    }

    public void setRelationType(String relationType) {
        this.relationType = relationType;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }

    public Boolean getSoftDeleted() {
        return softDeleted;
    }

    public void setSoftDeleted(Boolean softDeleted) {
        this.softDeleted = softDeleted;
    }
}
