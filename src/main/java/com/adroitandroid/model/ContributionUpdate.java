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

    public ContributionUpdate(Snippet snippet, Snippet parentSnippet, User user, SnippetStats snippetStats) {
        this.user = user;
        this.parentSnippet = parentSnippet;
        this.snippetStats = snippetStats;
        this.snippet = snippet;
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
        return snippet.createdAt;
    }
}
