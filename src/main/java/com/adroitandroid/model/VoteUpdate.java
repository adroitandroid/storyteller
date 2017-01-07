package com.adroitandroid.model;

import java.util.Date;

/**
 * Created by pv on 07/01/17.
 */
public class VoteUpdate {
    private final User user;
    private final Snippet parentSnippet;
    private final SnippetStats snippetStats;
    private final Snippet snippet;
    private final long voteSum;
    private final Date mostRecentUpdateTime;

    public VoteUpdate(Snippet snippet, Snippet parentSnippet, SnippetStats snippetStats, User user, long voteSum, Date mostRecentUpdateTime) {
        this.snippet = snippet;
        this.voteSum = voteSum;
        this.mostRecentUpdateTime = mostRecentUpdateTime;
        this.parentSnippet = parentSnippet;
        this.snippetStats = snippetStats;
        this.user = user;
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

    public long getVoteSum() {
        return voteSum;
    }

    public Date getMostRecentUpdateTime() {
        return mostRecentUpdateTime;
    }
}
