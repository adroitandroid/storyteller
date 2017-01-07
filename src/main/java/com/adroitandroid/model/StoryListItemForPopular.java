package com.adroitandroid.model;

/**
 * Created by pv on 07/01/17.
 */
/**
 * Response type, not entity
 */
public class StoryListItemForPopular extends StoryListItem {
    public StoryListItemForPopular(Story story) {
        super(story, StoryListItem.CATEGORY_POPULAR);
    }
}
