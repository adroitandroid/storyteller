package com.adroitandroid.model.service;

import com.adroitandroid.model.Snippet;
import com.adroitandroid.model.SnippetListItem;
import com.adroitandroid.model.Story;
import com.adroitandroid.model.UserSnippetVote;

import java.util.List;
import java.util.Set;

/**
 * Created by pv on 05/01/17.
 */
public interface SnippetService {

    Set<SnippetListItem> getSnippetsForFeed();

    Snippet addNewSnippet(Snippet snippet);

    UserSnippetVote addUserVote(UserSnippetVote userSnippetVote);

    List<SnippetListItem> getSnippetTreeWithRootId(long id);

    Story addNewEnd(Story story);
}
