package com.adroitandroid.model;

import com.adroitandroid.serializer.OptionalInGson;

import javax.persistence.*;

/**
 * Created by pv on 30/11/16.
 */
@Entity
@Table(name = "story_genres")
public class StoryGenre {
    public static final String GENRE = "genre_in_story_genres";
    public static final String STORY = "story_in_story_genres";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OptionalInGson(exclude = STORY)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "story_id")
    private StorySummary story;

    @OptionalInGson(exclude = GENRE)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "genre_id")
    private Genre genre;

    @Column(name = "count_completed")
    private Integer countCompleted;
}
