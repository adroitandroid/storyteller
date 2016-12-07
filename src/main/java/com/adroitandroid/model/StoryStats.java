package com.adroitandroid.model;

import com.adroitandroid.serializer.OptionalInGson;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by pv on 30/11/16.
 */

//TODO: Serializable can break things... because gson seems to be taking variable name and coverting to underscorecase
@Entity
@Table(name = "story_stats")
public class StoryStats implements Serializable {
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

    public StoryStats() {
//        Empty constructor required by hibernate
    }

    public StoryStats(Long storyId) {
        this.story = new StorySummary(storyId);
    }
}
