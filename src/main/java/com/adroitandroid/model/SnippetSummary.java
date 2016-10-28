package com.adroitandroid.model;

import java.io.Serializable;

/**
 * Created by pv on 28/10/16.
 */
public class SnippetSummary implements Serializable {
    private Long id;
    private Long parentId;
    private String snippetText;
    private Boolean isEnd;
    private Long userVotes;

    public SnippetSummary(Long id, Long parentId, String snippetText, Boolean isEnd, Long userVotes) {
        this.id = id;
        this.parentId = parentId;
        this.snippetText = snippetText;
        this.isEnd = isEnd;
        this.userVotes = userVotes;
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

    public Long getUserVotes() {
        return userVotes;
    }

    public void setUserVotes(Long userVotes) {
        this.userVotes = userVotes;
    }
}
