package com.adroitandroid.model;

import java.util.Date;

/**
 * Created by pv on 07/01/17.
 */
public class ContributionUpdate {
    private final User user;
    private final Snippet parentSnippet;
    private final SnippetStats snippetStats;
    private final Snippet snippet;
    private final Date createdTime;

    public ContributionUpdate(User user, Snippet parentSnippet, SnippetStats snippetStats, Snippet snippet, Date createdTime) {
        this.user = user;
        this.parentSnippet = parentSnippet;
        this.snippetStats = snippetStats;
        this.snippet = snippet;
        this.createdTime = createdTime;
    }

    public User getUser() {
        return user;
    }

    public Snippet getParentSnippet() {
        return parentSnippet;
    }

    public SnippetStats getSnippetStats() {
        return snippetStats;
    }

    public Snippet getSnippet() {
        return snippet;
    }

    public Date getCreatedTime() {
        return createdTime;
    }
}
