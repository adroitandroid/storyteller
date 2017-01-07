package com.adroitandroid.model;

/**
 * Created by pv on 07/01/17.
 */
/**
 * Response type, not entity
 */
public class StoryListItemForNew extends StoryListItem {
    public StoryListItemForNew(Story s) {
        super(s, StoryListItem.CATEGORY_NEW);
    }
}
