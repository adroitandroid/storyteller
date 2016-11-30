package com.adroitandroid.model;

import com.adroitandroid.OptionalInGson;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

/**
 * Created by pv on 29/11/16.
 */
@Entity
@Table(name = "prompt")
public class Prompt implements Serializable {

    public static final String STORIES = "stories";
    public static final String SOFT_DELETED = "soft_deleted";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }

    public Boolean getSoftDeleted() {
        return softDeleted;
    }

    public void setSoftDeleted(Boolean softDeleted) {
        this.softDeleted = softDeleted;
    }

    public List<StorySummary> getStories() {
        return stories;
    }

    public void setStories(List<StorySummary> stories) {
        this.stories = stories;
    }
}
