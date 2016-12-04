package com.adroitandroid.model;

import com.adroitandroid.OptionalInGson;
import com.adroitandroid.StringToJsonArrayAdapter;
import com.google.gson.Gson;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.reflect.TypeToken;

import javax.persistence.*;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

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

    public Chapter(String chapterTitle, String chapterPlot, Long previousChapterId, Long userId,
                   StorySummary storySummary, List<Long> traversal, Integer chapterStatus) {
        this.title = chapterTitle;
        this.description = chapterPlot;
        this.authorUserId = userId;
        this.parentChapterId = previousChapterId;
        this.softDeleted = false;
        this.status = chapterStatus;
        this.storySummary = storySummary;
        setTraversal(traversal);
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
    public Boolean endsStory;
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

    @Access(AccessType.FIELD)
    @JsonAdapter(StringToJsonArrayAdapter.class)
    private String traversal;

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

    public StorySummary getStorySummary() {
        return storySummary;
    }

    public List<Long> getTraversal() {
        return traversalStringToList();
    }

    public void setTraversal(List<Long> traversalList) {
        setTraversalStringFrom(traversalList);
    }

    public Long getId() {
        return id;
    }

    public ChapterDetail getDetail() {
        return detail;
    }

    private List<Long> traversalStringToList() {
        List<Long> traversedList;
        Type collectionType = new TypeToken<List<Long>>() {}.getType();
        Gson gson = new Gson();
        traversedList = gson.fromJson(traversal, collectionType);
        return traversedList;
    }

    private void setTraversalStringFrom(List<Long> traversalList) {
        Gson gson = new Gson();
        traversal = gson.toJson(traversalList);
    }
}
