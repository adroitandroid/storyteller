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
        this.snippet = snippet;
        this.parentSnippet = parentSnippet;
        this.author = authorUser;
        this.snippetStats = snippetStats;
        this.category = category;
    }

//    Used in SnippetRepository query
    public SnippetListItem(Snippet snippet, Snippet parentSnippet, SnippetStats snippetStats, User authorUser) {
        this.snippet = snippet;
        this.parentSnippet = parentSnippet;
        this.author = authorUser;
        this.snippetStats = snippetStats;
    }

    SnippetListItem(Snippet snippet, SnippetStats snippetStats, User authorUser, String category) {
        this.snippet = snippet;
        this.author = authorUser;
        this.snippetStats = snippetStats;
        this.category = category;
    }

    public SnippetStats getSnippetStats() {
        return snippetStats;
    }

    public String getCategory() {
        return this.category;
    }

    public Timestamp getSnippetCreationTime() {
        return this.snippet.createdAt;
    }

    public void removeParentIfDummy() {
        if (Snippet.DUMMY_SNIPPET_ID.equals(this.parentSnippet.getId())) {
            this.parentSnippet = null;
        }
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof SnippetListItem && this.snippet.getId().equals(((SnippetListItem)obj).snippet.getId());
    }
}
