package com.adroitandroid.model;

/**
 * Created by pv on 05/01/17.
 */
/**
 * Response type, not entity
 */
public class SnippetListItemForTrending extends SnippetListItem {
    public static final Long MIN_VOTES_FOR_TRENDING = 10L;

    public SnippetListItemForTrending(Snippet snippet, Snippet parentSnippet, SnippetStats snippetStats, User authorUser) {
        super(snippet, parentSnippet, snippetStats, authorUser, CATEGORY_TRENDING);
    }
}
