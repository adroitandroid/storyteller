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
    public Long id;

    @Access(AccessType.FIELD)
    private String content;

    private User authorUser;

    @Column(name = "parent_snippet_id")
    @Access(AccessType.FIELD)
    private Long parentSnippetId;

    @Column(name = "root_snippet_id")
    @Access(AccessType.FIELD)
    public Long rootSnippetId;

    @Column(name = "created_at")
    @Access(AccessType.FIELD)
    public Timestamp createdAt;

    @OptionalInGson(exclude = "snippet_stats")
    @OneToOne(mappedBy="snippet", cascade=CascadeType.ALL)
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

    public void init() {
        setCreatedTimeAsCurrent();
        this.snippetStats = new SnippetStats(this.createdAt);
        this.snippetStats.snippet = this;
    }
}
