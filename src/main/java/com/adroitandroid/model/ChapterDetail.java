package com.adroitandroid.model;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by pv on 30/11/16.
 */
@Entity
@Table(name = "chapter_detail")
public class ChapterDetail implements Serializable {

    public ChapterDetail(String content) {
        this.content = content;
        updateCreatedAndUpdatedTime();
    }

    public ChapterDetail() {
//        required by hibernate
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String content;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Column(name = "created_at")
    private Timestamp createdAt;

    public Long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    private void updateCreatedAndUpdatedTime() {
        this.createdAt = new Timestamp((new Date()).getTime());
        this.updatedAt = this.createdAt;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
