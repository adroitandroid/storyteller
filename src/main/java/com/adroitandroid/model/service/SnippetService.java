package com.adroitandroid.model.service;

import com.adroitandroid.model.Snippet;

import java.util.List;

/**
 * Created by pv on 05/01/17.
 */
public interface SnippetService {
    List<Snippet> getTrendingSnippetsForFeed();

    List<Snippet> getPopularSnippetsForFeed();

    List<Snippet> getNewSnippetsForFeed();

    List<Snippet> getNormalSnippetsForFeed();

    Snippet addNewSnippet(Snippet snippet);
}
