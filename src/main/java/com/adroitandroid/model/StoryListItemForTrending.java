package com.adroitandroid.model;

/**
 * Created by pv on 07/01/17.
 */
/**
 * Response type, not entity
 */
public class StoryListItemForTrending extends StoryListItem {
    public StoryListItemForTrending(Story story) {
        super(story, StoryListItem.CATEGORY_TRENDING);
    }
}
