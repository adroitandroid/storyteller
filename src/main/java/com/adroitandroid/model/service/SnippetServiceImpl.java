package com.adroitandroid.model.service;

import com.adroitandroid.model.*;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.*;

/**
 * Created by pv on 05/01/17.
 */
@Component("snippetService")
@Transactional
public class SnippetServiceImpl extends AbstractService implements SnippetService {
    private static final long MIN_VOTES_FOR_NOT_NEW = 6L;
    public static final int MIN_SNIPPET_LIST_SIZE = 40;
    private SnippetRepository snippetRepository;
    private UserRepository userRepository;

    public SnippetServiceImpl(SnippetRepository snippetRepository, UserRepository userRepository) {
        this.snippetRepository = snippetRepository;
        this.userRepository = userRepository;
    }

    public List<Snippet> getTrendingSnippetsForFeed() {
//        TODO: implement
        return null;
    }

    public List<Snippet> getPopularSnippetsForFeed() {
//        TODO: implement
        return null;
    }

    public Set<SnippetListItem> getSnippetsForFeed() {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, -1);
        Timestamp yesterday = new Timestamp(calendar.getTime().getTime());

        HashSet<SnippetListItem> snippetListItems = new HashSet<>();
        snippetListItems.addAll(getSnippetListItemSortedByCreateDate());
        snippetListItems.addAll(getSnippetListItemsForPopular());
        snippetListItems.addAll(getSnippetListItemsForNew(yesterday));
//    TODO: implement
//        snippetListItems.addAll(getSnippetListItemsForTrending(yesterday));
        for (SnippetListItem snippetListItem : snippetListItems) {
            snippetListItem.removeParentIfDummy();
        }
        return snippetListItems;
    }

    private List<SnippetListItem> getSnippetListItemSortedByCreateDate() {
        Type listType = new TypeToken<ArrayList<SnippetListItemForNew>>() {}.getType();
        List<SnippetListItem> snippetsForNewFeed = snippetRepository.findRecentlyCreatedStoriesOrSnippets(
                new PageRequest(0, MIN_SNIPPET_LIST_SIZE,
                new Sort(new Sort.Order(Sort.Direction.DESC, "createdAt")))).getContent();
        JsonElement jsonElement = prepareResponseFrom(snippetsForNewFeed);
        return new Gson().fromJson(jsonElement, listType);
    }

//    TODO: implement
//    private List<SnippetListItemForTrending> getSnippetListItemsForTrending(Timestamp yesterday) {
//        return null;
//    }

    private List<SnippetListItemForPopular> getSnippetListItemsForPopular() {
        Type listType = new TypeToken<ArrayList<SnippetListItemForNew>>() {}.getType();
        List<SnippetListItemForPopular> snippetsForNewFeed = snippetRepository.findSnippetsForPopularFeed();
        JsonElement jsonElement = prepareResponseFrom(snippetsForNewFeed);
        return new Gson().fromJson(jsonElement, listType);
    }

    private List<SnippetListItemForNew> getSnippetListItemsForNew(Timestamp yesterday) {
        Type listType = new TypeToken<ArrayList<SnippetListItemForNew>>() {}.getType();
        List<SnippetListItemForNew> snippetsForNewFeed
                = snippetRepository.findSnippetsForNewFeed(yesterday, MIN_VOTES_FOR_NOT_NEW);
        JsonElement jsonElement = prepareResponseFrom(snippetsForNewFeed);
        return new Gson().fromJson(jsonElement, listType);
    }

    public List<Snippet> getNormalSnippetsForFeed() {
//        TODO: implement
        return null;
    }

    @Override
    public Snippet addNewSnippet(Snippet snippet) {
        User user = userRepository.findOne(snippet.getAuthorUser().getId());
        snippet.setAuthorUser(user);
        snippet.init();
        if (snippet.getParentSnippetId() == null) {
            snippet.setParentSnippetId(-1L);
        }
        Snippet snippetInDb = snippetRepository.save(snippet);
        if (snippet.getRootSnippetId() == null) {
            snippetInDb.setRootSnippetId(snippetInDb.getId());
            snippetInDb = snippetRepository.save(snippetInDb);
        }
        return snippetInDb;
    }
}
