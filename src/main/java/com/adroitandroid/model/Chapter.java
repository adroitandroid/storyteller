package com.adroitandroid.model;

import com.adroitandroid.OptionalInGson;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by pv on 30/11/16.
 */
@Entity
@Table(name = "chapter_summary")
public class Chapter implements Serializable {
    public static final String CHAPTER_DETAIL = "chapter_detail_in_chapter_summary";
    public static final String STORY_SUMMARY = "story_summary_in_chapter_summary";

    public static final int STATUS_PUBLISHED = 3;
    public static final int STATUS_APPROVED = 1;
    public static final int STATUS_AUTO_APPROVED = 2;
    public static final int STATUS_UNAPPROVED = 0;
    public static final int STATUS_REJECTED = -1;

    public Chapter() {
//        default constructor required by hibernate
    }

    public Chapter(String chapterTitle, String chapterPlot, Long previousChapterId, Long userId, StorySummary storySummary) {
        this.title = chapterTitle;
        this.description = chapterPlot;
        this.authorUserId = userId;
        this.parentChapterId = previousChapterId;
        this.softDeleted = false;
        this.status = STATUS_UNAPPROVED;
        this.storySummary = storySummary;
        updateCreatedAndUpdatedTime();
    }

    private void updateCreatedAndUpdatedTime() {
        this.createdAt = new Timestamp((new Date()).getTime());
        this.updatedAt = this.createdAt;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "parent_id")
    private Long parentChapterId;
    private String title;
    private String description;

    @Column(name = "ends_story")
    private Boolean endsStory;
    private Integer status;

    @OneToOne(fetch=FetchType.LAZY)
    @OptionalInGson(exclude = CHAPTER_DETAIL)
    @JoinColumn(name = "detail_id")
    private ChapterDetail detail;

    @ManyToOne(fetch=FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name="story_chapters",
            joinColumns={@JoinColumn(name="chapter_id", referencedColumnName="id")},
            inverseJoinColumns={@JoinColumn(name="story_id", referencedColumnName="id")})
    @OptionalInGson(exclude = STORY_SUMMARY)
    private StorySummary storySummary;

    @Column(name = "author_user_id")
    private Long authorUserId;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "soft_deleted")
    private Boolean softDeleted;

    public Integer getStatus() {
        return status;
    }

    public Long getAuthorUserId() {
        return authorUserId;
    }
}
