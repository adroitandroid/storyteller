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
    public static final String CHAPTER_STATS = "chapter_stats_in_chapter_summary";

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

    private Long id;

    @Access(AccessType.FIELD)
    @Column(name = "parent_id")
    public Long parentChapterId;

    @Access(AccessType.FIELD)
    public String title;

    @Access(AccessType.FIELD)
    private String description;

    @Column(name = "ends_story")
    @Access(AccessType.FIELD)
    public Boolean endsStory;

    @Access(AccessType.FIELD)
    private Integer status;

    @OneToOne(fetch=FetchType.LAZY)
    @OptionalInGson(exclude = CHAPTER_DETAIL)
    @JoinColumn(name = "detail_id")
    @Access(AccessType.FIELD)
    private ChapterDetail detail;

    @ManyToOne(fetch=FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name="story_chapters",
            joinColumns={@JoinColumn(name="chapter_id", referencedColumnName="id")},
            inverseJoinColumns={@JoinColumn(name="story_id", referencedColumnName="id")})
    @OptionalInGson(exclude = STORY_SUMMARY)
    @Access(AccessType.FIELD)
    private StorySummary storySummary;

    @OptionalInGson(exclude = CHAPTER_STATS)
    @OneToOne(mappedBy="chapter", fetch=FetchType.LAZY)
    @Access(value = AccessType.FIELD)
    private ChapterStats chapterStats;

    @Column(name = "author_user_id")
    @Access(AccessType.FIELD)
    private Long authorUserId;

    @Access(AccessType.FIELD)
    @JsonAdapter(StringToJsonArrayAdapter.class)
    private String traversal;

    @Column(name = "updated_at")
    @Access(AccessType.FIELD)
    private Timestamp updatedAt;

    @Column(name = "created_at")
    @Access(AccessType.FIELD)
    private Timestamp createdAt;

    @Column(name = "soft_deleted")
    @Access(AccessType.FIELD)
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

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
