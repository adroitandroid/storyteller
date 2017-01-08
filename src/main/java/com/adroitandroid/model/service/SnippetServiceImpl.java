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
import java.util.stream.Collectors;

/**
 * Created by pv on 05/01/17.
 */
@Component("snippetService")
@Transactional
public class SnippetServiceImpl extends AbstractService implements SnippetService {
    private static final long MIN_VOTES_FOR_NOT_NEW = 6L;
    public static final int MIN_SNIPPET_LIST_SIZE = 40;
    private static final int MIN_FREQUENCY_FOR_TRENDING = 10;
    private SnippetRepository snippetRepository;
    private UserRepository userRepository;
    private UserSnippetVoteRepository userSnippetVoteRepository;
    private SnippetStatsRepository snippetStatsRepository;
    private SnippetRecentVoteRepository snippetRecentVoteRepository;
    private StoryRepository storyRepository;
    private StoryRecentVoteRepository storyRecentVoteRepository;
    private UserBookmarkRepository userBookmarkRepository;

    public SnippetServiceImpl(SnippetRepository snippetRepository,
                              UserRepository userRepository,
                              UserSnippetVoteRepository userSnippetVoteRepository,
                              SnippetStatsRepository snippetStatsRepository,
                              SnippetRecentVoteRepository snippetRecentVoteRepository,
                              StoryRepository storyRepository,
                              StoryRecentVoteRepository storyRecentVoteRepository,
                              UserBookmarkRepository userBookmarkRepository) {
        this.snippetRepository = snippetRepository;
        this.userRepository = userRepository;
        this.userSnippetVoteRepository = userSnippetVoteRepository;
        this.snippetStatsRepository = snippetStatsRepository;
        this.snippetRecentVoteRepository = snippetRecentVoteRepository;
        this.storyRepository = storyRepository;
        this.storyRecentVoteRepository = storyRecentVoteRepository;
        this.userBookmarkRepository = userBookmarkRepository;
    }

    public Set<SnippetListItem> getSnippetsForFeed(long userId) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, -1);
        Timestamp yesterday = new Timestamp(calendar.getTime().getTime());

        HashSet<SnippetListItem> snippetListItems = new HashSet<>();
        snippetListItems.addAll(getSnippetListItemsForTrending());
        snippetListItems.addAll(getSnippetListItemsForNew(yesterday));
        snippetListItems.addAll(getSnippetListItemsForPopular());
        snippetListItems.addAll(getSnippetListItemSortedByCreateDate());

        Map<Long, Integer> snippetVoteMap = new HashMap<>();
        Set<Long> bookmarkSet = new HashSet<>();
        if (userId > 0) {
            List<UserSnippetVote> userVotes = userSnippetVoteRepository.findByUserId(userId);
            for (UserSnippetVote snippetVote : userVotes) {
                snippetVoteMap.put(snippetVote.getSnippet().getId(), snippetVote.getVote());
            }
            List<UserBookmark> userBookmarks = userBookmarkRepository.findByUserIdAndSoftDeletedFalse(userId);
            for (UserBookmark userBookmark : userBookmarks) {
                bookmarkSet.add(userBookmark.getSnippet().getId());
            }
        }

        for (SnippetListItem snippetListItem : snippetListItems) {
            snippetListItem.removeParentIfDummy();
            snippetListItem.setUserVote(snippetVoteMap.get(snippetListItem.getSnippetId()));
            snippetListItem.setBookmarked(bookmarkSet.contains(snippetListItem.getSnippetId()));
        }
        return snippetListItems;
    }

    private List<SnippetListItem> getSnippetListItemSortedByCreateDate() {
        Type listType = new TypeToken<ArrayList<SnippetListItem>>() {}.getType();
        List<SnippetListItem> snippetsForNewFeed = snippetRepository.findRecentlyCreatedStoriesOrSnippets(
                new PageRequest(0, MIN_SNIPPET_LIST_SIZE,
                new Sort(new Sort.Order(Sort.Direction.DESC, "createdAt")))).getContent();
        JsonElement jsonElement = prepareResponseFrom(snippetsForNewFeed);
        return new Gson().fromJson(jsonElement, listType);
    }

    private List<StoryListItem> getStoryListItemsSortedByCreateDate() {
        Type listType = new TypeToken<ArrayList<StoryListItem>>() {}.getType();
        List<StoryListItem> snippetsForNewFeed = snippetRepository.findRecentlyCompletedStories(
                new PageRequest(0, MIN_SNIPPET_LIST_SIZE,
                new Sort(new Sort.Order(Sort.Direction.DESC, "createdAt")))).getContent();
        JsonElement jsonElement = prepareResponseFrom(snippetsForNewFeed,
                Snippet.AUTHOR_USER_IN_SNIPPET, Snippet.SNIPPET_STATS_IN_SNIPPET);
        return new Gson().fromJson(jsonElement, listType);
    }

    private List<SnippetListItemForTrending> getSnippetListItemsForTrending() {
        ArrayList<RecentSnippet> recentSnippets = new ArrayList<>(snippetRecentVoteRepository.getRecentSnippets());
        int i = 0;
        while (i < recentSnippets.size()) {
            if (recentSnippets.get(i).getFrequency() >= MIN_FREQUENCY_FOR_TRENDING) {
                i++;
            } else {
                recentSnippets.remove(i);
            }
        }
        if (recentSnippets.size() > 0) {
            Set<Long> snippetIdSet = recentSnippets.stream().map(RecentSnippet::getSnippetId).collect(Collectors.toSet());
            List<SnippetListItemForTrending> trendingSnippetListItems = snippetRepository.findSnippetsWithIds(snippetIdSet);

            Type listType = new TypeToken<ArrayList<SnippetListItemForTrending>>() {
            }.getType();
            JsonElement jsonElement = prepareResponseFrom(trendingSnippetListItems);
            return new Gson().fromJson(jsonElement, listType);
        } else {
            return new ArrayList<>();
        }
    }

    private List<StoryListItemForTrending> getStoryListItemsForTrending() {
        ArrayList<RecentSnippet> recentStories = new ArrayList<>(storyRecentVoteRepository.getRecentStories());
        int i = 0;
        while (i < recentStories.size()) {
            if (recentStories.get(i).getFrequency() >= MIN_FREQUENCY_FOR_TRENDING) {
                i++;
            } else {
                recentStories.remove(i);
            }
        }
        if (recentStories.size() > 0) {
            Set<Long> snippetIdSet = recentStories.stream().map(RecentSnippet::getSnippetId).collect(Collectors.toSet());
            List<StoryListItemForTrending> trendingStoryListItems = storyRepository.findWithEndSnippetIdIn(snippetIdSet);

            Type listType = new TypeToken<ArrayList<StoryListItemForTrending>>() {
            }.getType();
            JsonElement jsonElement = prepareResponseFrom(trendingStoryListItems,
                    Snippet.AUTHOR_USER_IN_SNIPPET, Snippet.SNIPPET_STATS_IN_SNIPPET);
            return new Gson().fromJson(jsonElement, listType);
        } else {
            return new ArrayList<>();
        }
    }

    private List<SnippetListItemForPopular> getSnippetListItemsForPopular() {
        Type listType = new TypeToken<ArrayList<SnippetListItemForPopular>>() {}.getType();
        List<SnippetListItemForPopular> snippetsForNewFeed = snippetRepository.findSnippetsForPopularFeed();
        JsonElement jsonElement = prepareResponseFrom(snippetsForNewFeed);
        return new Gson().fromJson(jsonElement, listType);
    }

    private List<StoryListItemForPopular> getStoryListItemsForPopular() {
        Type listType = new TypeToken<ArrayList<StoryListItemForPopular>>() {}.getType();
        List<StoryListItemForPopular> snippetsForNewFeed = snippetRepository.findStoriesForPopularFeed();
        JsonElement jsonElement = prepareResponseFrom(snippetsForNewFeed,
                Snippet.AUTHOR_USER_IN_SNIPPET, Snippet.SNIPPET_STATS_IN_SNIPPET);
        return new Gson().fromJson(jsonElement, listType);
    }

    private List<SnippetListItemForNew> getSnippetListItemsForNew(Timestamp yesterday) {
        Type listType = new TypeToken<ArrayList<SnippetListItemForNew>>() {}.getType();
        List<SnippetListItemForNew> snippetsForNewFeed
                = snippetRepository.findSnippetsForNewFeed(yesterday, MIN_VOTES_FOR_NOT_NEW);
        JsonElement jsonElement = prepareResponseFrom(snippetsForNewFeed);
        return new Gson().fromJson(jsonElement, listType);
    }

    private List<StoryListItemForNew> getStoryListItemsForNew(Timestamp yesterday) {
        Type listType = new TypeToken<ArrayList<StoryListItemForNew>>() {}.getType();
        List<StoryListItemForNew> storiesForNewFeed
                = snippetRepository.findStoriesForNewFeed(yesterday, MIN_VOTES_FOR_NOT_NEW);
        JsonElement jsonElement = prepareResponseFrom(storiesForNewFeed,
                Snippet.AUTHOR_USER_IN_SNIPPET, Snippet.SNIPPET_STATS_IN_SNIPPET);
        return new Gson().fromJson(jsonElement, listType);
    }

    @Override
    public Snippet addNewSnippet(Snippet snippet) {
        User user = userRepository.findOne(snippet.getAuthorUser().getId());
        snippet.setAuthorUser(user);
        snippet.init(false);
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

    @Override
    public UserSnippetVote addUserVote(UserSnippetVote userSnippetVote) {
        Timestamp currentTime = new Timestamp((new Date()).getTime());
        Long snippetId = userSnippetVote.getSnippet().getId();
        Long userId = userSnippetVote.getUser().getId();
        Integer newVote = userSnippetVote.getVote();

        if (userSnippetVote.getSnippet().endsStory) {
            StoryRecentVote storyRecentVote = new StoryRecentVote(snippetId, userId);
            StoryRecentVote existingUserStoryVote = storyRecentVoteRepository.findByUserIdAndSnippetId(userId, snippetId);
            if (existingUserStoryVote != null) {
                storyRecentVoteRepository.delete(existingUserStoryVote);
            }
            storyRecentVoteRepository.save(storyRecentVote);
        } else {
            SnippetRecentVote snippetRecentVote = new SnippetRecentVote(snippetId, userId);
            SnippetRecentVote existingUserSnippetVote = snippetRecentVoteRepository.findByUserIdAndSnippetId(userId, snippetId);
            if (existingUserSnippetVote != null) {
                snippetRecentVoteRepository.delete(existingUserSnippetVote);
            }
            snippetRecentVoteRepository.save(snippetRecentVote);
        }

        UserSnippetVote existingVote = userSnippetVoteRepository.findByUserIdAndSnippetId(userId, snippetId);

        Integer deltaVote;
        if (existingVote == null) {
            deltaVote = newVote;
            snippetStatsRepository.updateVotes(snippetId, 1, deltaVote, currentTime);
            userSnippetVote.setSnippet(snippetRepository.findOne(snippetId));
            userSnippetVote.setUser(userRepository.findOne(userId));
        } else {
            deltaVote = newVote - existingVote.getVote();
            snippetStatsRepository.updateVotes(snippetId, 0, deltaVote, currentTime);
            existingVote.setVote(userSnippetVote.getVote());
            userSnippetVote = existingVote;
        }
        userSnippetVote.setUpdatedAt(currentTime);
        UserSnippetVote savedVote = userSnippetVoteRepository.save(userSnippetVote);

        JsonElement jsonElement
                = prepareResponseFrom(savedVote, UserSnippetVote.SNIPPET_IN_USER_VOTES, Snippet.SNIPPET_STATS_IN_SNIPPET);
        UserSnippetVote snippetVote = new Gson().fromJson(jsonElement, UserSnippetVote.class);
//        TODO: snippet stats is being retrieved from within transaction
        snippetVote.getSnippet().getSnippetStats().setVoteSum(
                snippetVote.getSnippet().getSnippetStats().getVoteSum() + deltaVote);
        return snippetVote;
    }

    @Override
    public List<SnippetListItem> getSnippetTreeWithRootId(long id) {
        Type listType = new TypeToken<ArrayList<SnippetListItem>>() {}.getType();
        List<SnippetListItem> snippetListForRoot = snippetRepository.findByRootSnippetId(id);
        for (SnippetListItem snippetListItem : snippetListForRoot) {
            snippetListItem.removeParentIfDummy();
        }
        JsonElement jsonElement = prepareResponseFrom(snippetListForRoot);
        return new Gson().fromJson(jsonElement, listType);
    }

    @Override
    public Story addNewEnd(Story story) {
        User user = userRepository.findOne(story.getEndSnippet().getAuthorUser().getId());
        story.getEndSnippet().setAuthorUser(user);
        story.getEndSnippet().init(true);
        story.setCreatedAt(story.getEndSnippet().createdAt);
////        Not required since story cannot be one snippet long
//        if (story.getEndSnippet().getParentSnippetId() == null) {
//            story.getEndSnippet().setParentSnippetId(-1L);
//        }
        Story storyInDb = storyRepository.save(story);
////        Not required since story cannot be one snippet long
//        if (story.getEndSnippet().getRootSnippetId() == null) {
//            storyInDb.getEndSnippet().setRootSnippetId(storyInDb.getId());
//            storyInDb.setEndSnippet(snippetRepository.save(storyInDb.getEndSnippet()));
//        }
        return storyInDb;
    }

    @Override
    public Set<StoryListItem> getStoriesForFeed(Long userId) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, -1);
        Timestamp yesterday = new Timestamp(calendar.getTime().getTime());

        HashSet<StoryListItem> storyListItems = new HashSet<>();
        storyListItems.addAll(getStoryListItemsForTrending());
        storyListItems.addAll(getStoryListItemsForNew(yesterday));
        storyListItems.addAll(getStoryListItemsForPopular());
        storyListItems.addAll(getStoryListItemsSortedByCreateDate());

        Map<Long, Integer> snippetVoteMap = new HashMap<>();
        Set<Long> bookmarkSet = new HashSet<>();
        if (userId > 0) {
            List<UserSnippetVote> userVotes = userSnippetVoteRepository.findByUserId(userId);
            for (UserSnippetVote snippetVote : userVotes) {
                snippetVoteMap.put(snippetVote.getSnippet().getId(), snippetVote.getVote());
            }
            List<UserBookmark> userBookmarks = userBookmarkRepository.findByUserIdAndSoftDeletedFalse(userId);
            for (UserBookmark userBookmark : userBookmarks) {
                bookmarkSet.add(userBookmark.getSnippet().getId());
            }
        }

        for (StoryListItem storyListItem : storyListItems) {
//        This should not be required since any story ender will have a parent
//            storyListItem.story.getEndSnippet().removeParentIfDummy();
            storyListItem.setUserVote(snippetVoteMap.get(storyListItem.getSnippetId()));
            storyListItem.setBookmarked(bookmarkSet.contains(storyListItem.getSnippetId()));
        }

        return storyListItems;
    }
}
