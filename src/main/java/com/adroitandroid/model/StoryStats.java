package com.adroitandroid.model;

import com.adroitandroid.OptionalInGson;

import javax.persistence.*;

/**
 * Created by pv on 30/11/16.
 */
@Entity
@Table(name = "story_stats")
public class StoryStats {
    public static final String STORY = "story_in_story_stats";

    @Id
    private Long id;

    @OptionalInGson(exclude = STORY)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "story_id")
    private StorySummary story;

    @Column(name = "num_completed")
    private Integer numCompletedBranches;

    @Column(name = "num_likes")
    private Long numLikes;

    @Column(name = "num_reads")
    private Long numReads;
}
