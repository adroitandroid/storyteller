package com.adroitandroid.model;

import java.util.Date;

/**
 * Created by pv on 07/01/17.
 */
public class ChildUpdate {
    private final Snippet parentSnippet;
    private final SnippetStats snippetStats;
    private final Snippet snippet;
    private final long numChildren;
    private final Date mostRecentUpdateTime;
    private final User user;

    public ChildUpdate(Snippet snippet, Snippet parentSnippet, SnippetStats snippetStats, User user, long numNewChildren, Date mostRecent) {
        this.parentSnippet = parentSnippet;
        this.snippetStats = snippetStats;
        this.snippet = snippet;
        this.numChildren = numNewChildren;
        this.mostRecentUpdateTime = mostRecent;
        this.user = user;
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

    public long getNumChildren() {
        return numChildren;
    }

    public Date getMostRecentUpdateTime() {
        return mostRecentUpdateTime;
    }

    public User getUser() {
        return user;
    }
}
