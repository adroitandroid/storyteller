package com.adroitandroid.model;

import com.adroitandroid.serializer.OptionalInGson;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by pv on 05/01/17.
 */
@Entity
@Table(name = "snippet")
public class Snippet implements Serializable {
    public static final String SNIPPET_STATS_IN_SNIPPET = "snippet_stats_in_snippet";
    public static final String AUTHOR_USER_IN_SNIPPET = "author_user_in_snippet";
    public static final Long DUMMY_SNIPPET_ID = -1L;
    public Long id;

    @Access(AccessType.FIELD)
    private String content;

    @OptionalInGson(exclude = AUTHOR_USER_IN_SNIPPET)
    private User authorUser;

    @Column(name = "parent_snippet_id")
    @Access(AccessType.FIELD)
    private Long parentSnippetId;

    @Column(name = "root_snippet_id")
    @Access(AccessType.FIELD)
    public Long rootSnippetId;

    @Column(name = "ends_story")
    @Access(AccessType.FIELD)
    public Boolean endsStory;

    @Column(name = "created_at")
    @Access(AccessType.FIELD)
    public Timestamp createdAt;

    @OneToOne(mappedBy="snippet", cascade=CascadeType.ALL, fetch = FetchType.LAZY)
    @OptionalInGson(exclude = SNIPPET_STATS_IN_SNIPPET)
    @Access(value = AccessType.FIELD)
    private SnippetStats snippetStats;

    public Snippet() {
//        default constructor required by hibernate
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name = "author_user_id")
    public User getAuthorUser() {
        return authorUser;
    }

    public void setAuthorUser(User authorUser) {
        this.authorUser = authorUser;
    }

    public Long getParentSnippetId() {
        return parentSnippetId;
    }

    public void setParentSnippetId(Long parentSnippetId) {
        this.parentSnippetId = parentSnippetId;
    }

    public Long getRootSnippetId() {
        return rootSnippetId;
    }

    public void setRootSnippetId(Long rootSnippetId) {
        this.rootSnippetId = rootSnippetId;
    }

    private void setCreatedTimeAsCurrent() {
        this.createdAt = new Timestamp((new Date()).getTime());
    }

    public void init(boolean endsStory) {
        setCreatedTimeAsCurrent();
        this.snippetStats = new SnippetStats(this.createdAt);
        this.snippetStats.snippet = this;
        this.endsStory = endsStory;
        this.id = null;
    }

    public SnippetStats getSnippetStats() {
        return snippetStats;
    }
}
