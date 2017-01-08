package com.adroitandroid.model.service;

import com.adroitandroid.model.*;

import java.util.List;
import java.util.Set;

/**
 * Created by pv on 05/01/17.
 */
public interface SnippetService {

    Set<SnippetListItem> getSnippetsForFeed(long userId);

    Snippet addNewSnippet(Snippet snippet);

    UserSnippetVote addUserVote(UserSnippetVote userSnippetVote);

    List<SnippetListItem> getSnippetTreeWithRootId(long id);

    Story addNewEnd(Story story);

    Set<StoryListItem> getStoriesForFeed(Long userId);
}
