package com.adroitandroid.model;

import com.adroitandroid.serializer.OptionalInGson;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * Created by pv on 30/11/16.
 */
@Entity
@Table(name = "story_summary")
public class StorySummary implements Serializable {
    public static final String CHAPTERS = "chapters_in_story_summary";
    public static final String PROMPT = "prompt_in_story_summary";
    public static final String STORY_GENRES = "story_genres_in_story_summary";
    public static final String STORY_STATS = "story_stats_in_story_summary";

    public Long id;

    private String title;

    @OptionalInGson(exclude = CHAPTERS)
    private List<Chapter> chapters;

    @ManyToOne(fetch=FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name="prompt_stories",
            joinColumns={@JoinColumn(name="story_id", referencedColumnName="id")},
            inverseJoinColumns={@JoinColumn(name="prompt_id", referencedColumnName="id")})
    @OptionalInGson(exclude = PROMPT)
    @Access(value = AccessType.FIELD)
    private Prompt prompt;

    @OptionalInGson(exclude = STORY_GENRES)
    @OneToMany(mappedBy="story", fetch=FetchType.LAZY)
    @Access(value = AccessType.FIELD)
    private List<StoryGenre> storyGenres;

    @OptionalInGson(exclude = STORY_STATS)
    @OneToOne(mappedBy="story", fetch=FetchType.LAZY)
    @Access(value = AccessType.FIELD)
    private StoryStats storyStats;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    public StorySummary() {
//        default contructor required by hibernate
    }

    public StorySummary(String storyTitle, Prompt promptFromDb) {
        this.title = storyTitle;
        this.prompt = promptFromDb;
        updateCreatedAndUpdatedTime();
    }

    public StorySummary(Long storySummaryId) {
        this.id = storySummaryId;
    }

    @OneToMany(fetch=FetchType.LAZY)
    @JoinTable(name="story_chapters",
            joinColumns={@JoinColumn(name="story_id", referencedColumnName="id")},
            inverseJoinColumns={@JoinColumn(name="chapter_id", referencedColumnName="id")})
    public List<Chapter> getChapters() {
        return chapters;
    }

    public void setChapters(List<Chapter> chapters) {
        this.chapters = chapters;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    private void updateCreatedAndUpdatedTime() {
        this.createdAt = new Timestamp((new Date()).getTime());
        this.updatedAt = this.createdAt;
    }
}
