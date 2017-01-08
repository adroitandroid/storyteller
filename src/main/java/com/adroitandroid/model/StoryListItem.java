package com.adroitandroid.model;

/**
 * Created by pv on 05/01/17.
 */

import java.sql.Timestamp;

/**
 * Response type, not entity
 */
public class StoryListItem {
    public static final String CATEGORY_NEW = "new";
    public static final String CATEGORY_POPULAR = "popular";
    public static final String CATEGORY_TRENDING = "trending";

    private Story story;
    private int userVote;
    private boolean isBookmarked;
    private String category;

    StoryListItem(Story story, String category) {
        this.story = story;
        this.category = category;
    }

    public StoryListItem(Story story) {
        this.story = story;
    }

    public String getCategory() {
        return category;
    }

    public Timestamp getSnippetCreationTime() {
        return story.getEndSnippet().createdAt;
    }

    public Timestamp getEndSnippetUpdateTime() {
        return story.getEndSnippet().getSnippetStats().getUpdatedAt();
    }

    public Long getSnippetId() {
        return story.getEndSnippet().getId();
    }

    public void setUserVote(Integer userVote) {
        if (userVote != null) {
            this.userVote = userVote;
        }
    }

    public void setBookmarked(boolean bookmarked) {
        this.isBookmarked = bookmarked;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof StoryListItem && this.story.getId().equals(((StoryListItem)obj).story.getId());
    }

    @Override
    public int hashCode() {
        return this.story.getId().hashCode();
    }
}
