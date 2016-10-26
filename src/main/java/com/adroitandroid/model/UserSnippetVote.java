package com.adroitandroid.model;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Timestamp;

/**
 * Created by pv on 25/10/16.
 */
@Entity
@Table(name = "user_snippet_vote")
public class UserSnippetVote {

    @EmbeddedId
    private UserSnippetId userSnippetId;

    private Integer vote;

    @Column(name = "created_at")
    private Timestamp createTime;

    @Column(name = "updated_at")
    private Timestamp updateTime;

    public UserSnippetVote() {
    }

    public UserSnippetVote(UserSnippetVote vote) {

    }

    public UserSnippetId getUserSnippetId() {
        return userSnippetId;
    }

    public void setUserSnippetId(UserSnippetId userSnippetId) {
        this.userSnippetId = userSnippetId;
    }

    public Integer getVote() {
        return vote;
    }

    public void setVote(Integer vote) {
        this.vote = vote;
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
}
