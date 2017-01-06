package com.adroitandroid.model;

/**
 * Created by pv on 05/01/17.
 */
/**
 * Response type, not entity
 */
public class SnippetListItemForNew extends SnippetListItem {

    public SnippetListItemForNew(Snippet snippet, Snippet parentSnippet, SnippetStats snippetStats, User authorUser) {
        super(snippet, parentSnippet, snippetStats, authorUser, CATEGORY_NEW);
    }
}
