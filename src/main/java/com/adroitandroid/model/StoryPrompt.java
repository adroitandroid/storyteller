package com.adroitandroid.model;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;

/**
 * Created by pv on 25/10/16.
 */
@Entity
@Table(name = "story_prompt")
public class StoryPrompt implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String prompt;

    @Column(name = "start_date")
    private Date startDate;

    @Column(name = "end_date")
    private Date endDate;

    @Column(name = "created_at")
    private Timestamp createTime;

    @Column(name = "updated_at")
    private Timestamp updateTime;

    @Column(name = "soft_deleted")
    private Boolean softDeleted;

    public StoryPrompt(String promptText, Date startDate, Date endDate) {
        this.prompt = promptText;
        this.startDate = startDate;
        this.endDate = endDate;
        update();
        this.createTime = this.getUpdateTime();
//        TODO: this means that any new prompt added automatically will be soft_deleted initially and only on moderation become non-deleted
        this.softDeleted = true;
    }

    public StoryPrompt(Long id, String prompt, java.util.Date startDate, java.util.Date endDate,
                       Timestamp createTime, Timestamp updateTime, Boolean softDeleted) {
        this.id = id;
        this.prompt = prompt;
        this.startDate = new Date(startDate.getTime());
        this.endDate = new Date(endDate.getTime());
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.softDeleted = softDeleted;
    }

    public StoryPrompt() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
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

    public void update() {
        this.updateTime = new Timestamp((new java.util.Date()).getTime());
    }
}
