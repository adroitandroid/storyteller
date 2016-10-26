package com.adroitandroid.model;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Timestamp;

/**
 * Created by pv on 25/10/16.
 */
@Entity
@Table(name = "story_prompt")
public class StoryPrompt {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

//    @Column(name = "content")
    private String prompt;

    @Column(name = "start_date")
    private Date startDate;

    @Column(name = "end_date")
    private Date endDate;

    @Column(name = "created_at")
    private Timestamp createDate;

    @Column(name = "updated_at")
    private Timestamp updateDate;

    public StoryPrompt(String promptText, Date startDate, Date endDate) {
        this.prompt = promptText;
        this.startDate = startDate;
        this.endDate = endDate;
        update();
        this.createDate = this.getUpdateDate();
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

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    public Timestamp getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Timestamp updateDate) {
        this.updateDate = updateDate;
    }

    public void update() {
        this.updateDate = new Timestamp((new java.util.Date()).getTime());
    }
}
