package com.adroitandroid.model;

import com.adroitandroid.OptionalInGson;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

/**
 * Created by pv on 30/11/16.
 */
@Entity
@Table(name = "story_summary")
public class StorySummary implements Serializable {
    public static final String CHAPTERS = "chapters_in_story_summary";
    public static final String PROMPT = "prompt_in_story_summary";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String title;

    @OneToMany(fetch=FetchType.LAZY)
    @JoinTable(name="story_chapters",
            joinColumns={@JoinColumn(name="story_id", referencedColumnName="id")},
            inverseJoinColumns={@JoinColumn(name="chapter_id", referencedColumnName="id")})
    @OptionalInGson(exclude = CHAPTERS)
    private List<Chapter> chapters;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinTable(name="prompt_stories",
            joinColumns={@JoinColumn(name="story_id", referencedColumnName="id")},
            inverseJoinColumns={@JoinColumn(name="prompt_id", referencedColumnName="id")})
    @OptionalInGson(exclude = PROMPT)
    private Prompt prompt;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    public List<Chapter> getChapters() {
        return chapters;
    }
}
