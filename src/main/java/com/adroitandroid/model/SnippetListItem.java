package com.adroitandroid.model;

/**
 * Created by pv on 05/01/17.
 */

import java.sql.Timestamp;

/**
 * Response type, not entity
 */
public class SnippetListItem {
    public static final String CATEGORY_NEW = "new";
    public static final String CATEGORY_POPULAR = "popular";
    public static final String CATEGORY_TRENDING = "trending";
    private User author;
    private SnippetStats snippetStats;
    private Snippet snippet;
    private Snippet parentSnippet;
    private int userVote;
    private boolean isBookmarked;
    private String category;

    SnippetListItem(Snippet snippet, Snippet parentSnippet, SnippetStats snippetStats, User authorUser, String category) {
        basicInit(snippet, parentSnippet, snippetStats, authorUser);
        this.category = category;
    }

//    Used in SnippetRepository query
    public SnippetListItem(Snippet snippet, Snippet parentSnippet, SnippetStats snippetStats, User authorUser) {
        basicInit(snippet, parentSnippet, snippetStats, authorUser);
    }

    SnippetListItem(Snippet snippet, SnippetStats snippetStats, User authorUser, String category) {
        this.snippet = snippet;
        this.author = authorUser;
        this.snippetStats = snippetStats;
        this.category = category;
    }

    public SnippetListItem() {

    }

    void basicInit(Snippet snippet, Snippet parentSnippet, SnippetStats snippetStats, User authorUser) {
        this.snippet = snippet;
        this.parentSnippet = parentSnippet;
        this.author = authorUser;
        this.snippetStats = snippetStats;
    }

    public SnippetStats getSnippetStats() {
        return snippetStats;
    }

    public String getCategory() {
        return this.category;
    }

    void setCategory(String category) {
        this.category = category;
    }

    public Timestamp getSnippetCreationTime() {
        return this.snippet.createdAt;
    }

    public void removeParentIfDummy() {
        if (isStoryStarter()) {
            this.parentSnippet = null;
        }
    }

    boolean isStoryStarter() {
        return Snippet.DUMMY_SNIPPET_ID.equals(this.parentSnippet.getId());
    }

    public long getSnippetId() {
        return snippet.getId();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof SnippetListItem && this.snippet.getId().equals(((SnippetListItem)obj).snippet.getId());
    }

    @Override
    public int hashCode() {
        return this.snippet.getId().hashCode();
    }

    public void setUserVote(Integer userVote) {
        if (userVote != null) {
            this.userVote = userVote;
        }
    }

    public void setBookmarked(boolean bookmarked) {
        this.isBookmarked = bookmarked;
    }
}
