package com.adroitandroid.model.service;

import com.adroitandroid.model.Snippet;
import com.adroitandroid.model.SnippetListItem;

import java.util.List;
import java.util.Set;

/**
 * Created by pv on 05/01/17.
 */
public interface SnippetService {
    List<Snippet> getTrendingSnippetsForFeed();

    List<Snippet> getPopularSnippetsForFeed();

    Set<SnippetListItem> getSnippetsForFeed();

    List<Snippet> getNormalSnippetsForFeed();

    Snippet addNewSnippet(Snippet snippet);
}
