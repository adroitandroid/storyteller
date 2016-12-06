package com.adroitandroid.model;

import com.adroitandroid.OptionalInGson;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * Created by pv on 29/11/16.
 */
@Entity
@Table(name = "prompt")
public class Prompt implements Serializable {

    public static final String STORIES = "stories_in_prompt";
    public static final String SOFT_DELETED = "soft_deleted_in_prompt";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    private String content;

    @OneToMany(fetch=FetchType.LAZY)
    @JoinTable(name="prompt_stories",
            joinColumns={@JoinColumn(name="prompt_id", referencedColumnName="id")},
            inverseJoinColumns={@JoinColumn(name="story_id", referencedColumnName="id")})
    @OptionalInGson(exclude = STORIES)
    private List<StorySummary> stories;

    @Column(name = "created_at")
    private Timestamp createTime;

    @Column(name = "updated_at")
    private Timestamp updateTime;

    @Column(name = "soft_deleted")
    @OptionalInGson(exclude = SOFT_DELETED)
    private Boolean softDeleted;

    public Prompt() {
//        empty constructor required by hibernate
    }

    public Prompt(Long promptId) {
        this.id = promptId;
        updateCreatedAndUpdatedTime();
    }

    private void updateCreatedAndUpdatedTime() {
        this.createTime = new Timestamp((new Date()).getTime());
        this.updateTime = this.createTime;
    }

    public List<StorySummary> getStories() {
        return stories;
    }
}
