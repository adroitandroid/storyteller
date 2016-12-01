package com.adroitandroid.model;

import com.adroitandroid.OptionalInGson;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by pv on 02/12/16.
 */
@Entity
@Table(name = "user_story_relation")
public class UserStoryRelation {
    public static final String USER = "user_in_user_story_relation";
    public static final String STORY_SUMMARY = "story_in_user_story_relation";

    public static final Integer RELATION_ID_LIKE = 1;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch= FetchType.LAZY)
    @OptionalInGson(exclude = USER)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch= FetchType.LAZY)
    @OptionalInGson(exclude = STORY_SUMMARY)
    @JoinColumn(name = "story_id")
    private StorySummary storySummary;

    @Column(name = "relation_id")
    private Integer relationId;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Column(name = "soft_deleted")
    private Boolean softDeleted;
}
