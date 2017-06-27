package com.adroitandroid.model.service;

import com.adroitandroid.model.SnippetListItem;
import com.adroitandroid.model.UserBookmark;
import com.adroitandroid.model.UserSnippetVote;
import com.adroitandroid.serializer.GsonExclusionStrategy;
import com.adroitandroid.serializer.HibernateProxyTypeAdapter;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by pv on 05/01/17.
 */
@Service
@Transactional
public abstract class AbstractService {
    JsonElement prepareResponseFrom(Object src, String... includeAnnotated) {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapterFactory(HibernateProxyTypeAdapter.FACTORY);
        return builder.setExclusionStrategies(new GsonExclusionStrategy(includeAnnotated)).create().toJsonTree(src);
    }

    void updateBookmarkAndVoteStatusFor(long userId, Collection<SnippetListItem> snippetListItems, boolean allBookmarked) {
        Map<Long, Integer> snippetVoteMap = new HashMap<>();
        Set<Long> bookmarkSet = new HashSet<>();
        if (userId > 0) {
            List<UserSnippetVote> userVotes = getUserSnippetVoteRepository().findByUserId(userId);
            for (UserSnippetVote snippetVote : userVotes) {
                snippetVoteMap.put(snippetVote.getSnippet().getId(), snippetVote.getVote());
            }
            if (!allBookmarked) {
                List<UserBookmark> userBookmarks = getUserBookmarkRepository().findByUserIdAndSoftDeletedFalse(userId);
                for (UserBookmark userBookmark : userBookmarks) {
                    bookmarkSet.add(userBookmark.getSnippet().getId());
                }
            }
        }

        for (SnippetListItem snippetListItem : snippetListItems) {
            snippetListItem.removeParentIfDummy();
            snippetListItem.setUserVote(snippetVoteMap.get(snippetListItem.getSnippetId()));
            snippetListItem.setBookmarked(allBookmarked || bookmarkSet.contains(snippetListItem.getSnippetId()));
        }
    }

    protected abstract UserBookmarkRepository getUserBookmarkRepository();

    protected abstract UserSnippetVoteRepository getUserSnippetVoteRepository();
}
