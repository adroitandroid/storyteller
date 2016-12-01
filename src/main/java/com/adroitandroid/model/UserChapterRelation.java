package com.adroitandroid.model;

import com.adroitandroid.OptionalInGson;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by pv on 02/12/16.
 */
@Entity
@Table(name = "user_chapter_relation")
public class UserChapterRelation {
    public static final String USER = "user_in_user_chapter_relation";
    public static final String CHAPTER_SUMMARY = "chapter_in_user_chapter_relation";

    public static final Integer RELATION_ID_BOOKMARK = 1;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch= FetchType.LAZY)
    @OptionalInGson(exclude = USER)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch= FetchType.LAZY)
    @OptionalInGson(exclude = CHAPTER_SUMMARY)
    @JoinColumn(name = "chapter_id")
    private Chapter chapter;

    @Column(name = "relation_id")
    private Integer relationId;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Column(name = "soft_deleted")
    private Boolean softDeleted;
}
