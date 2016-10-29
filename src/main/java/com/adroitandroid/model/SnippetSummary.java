package com.adroitandroid.model;

import java.io.Serializable;
import java.util.HashSet;

/**
 * Created by pv on 28/10/16.
 */
public class SnippetSummary implements Serializable {
    private Long id;
    private Long parentId;
    private String snippetText;
    private Boolean isEnd;
    private Long voteSum;
    private Long voteCount;
    private HashSet<Story> storiesFromSnippet;

    public SnippetSummary(Long id, Long parentId, String snippetText, Boolean isEnd, Long voteSum, Long voteCount) {
        this.id = id;
        this.parentId = parentId;
        this.snippetText = snippetText;
        this.isEnd = isEnd;
        this.voteSum = voteSum;
        this.voteCount = voteCount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getSnippetText() {
        return snippetText;
    }

    public void setSnippetText(String snippetText) {
        this.snippetText = snippetText;
    }

    public Boolean getEnd() {
        return isEnd;
    }

    public void setEnd(Boolean end) {
        isEnd = end;
    }

    public Long getVoteSum() {
        return voteSum;
    }

    public void setVoteSum(Long voteSum) {
        this.voteSum = voteSum;
    }

    public Long getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(Long voteCount) {
        this.voteCount = voteCount;
    }

    public HashSet<Story> getStoriesFromSnippet() {
        return storiesFromSnippet;
    }

    public void setStoriesFromSnippet(HashSet<Story> storiesFromSnippet) {
        this.storiesFromSnippet = storiesFromSnippet;
    }
}
