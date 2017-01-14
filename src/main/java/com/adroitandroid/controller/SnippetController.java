package com.adroitandroid.controller;

import com.adroitandroid.model.*;
import com.adroitandroid.model.service.SnippetService;
import com.google.gson.JsonElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pv on 05/01/17.
 */
@RestController
@RequestMapping(value = "/snippet")
public class SnippetController extends AbstractController {
    @Autowired
    private SnippetService snippetService;

    /**
     * Defining new as snippets created within last day OR having less than 5 num votes; least recent created first, new is FIFO
     * Defining trending as those getting > 10 num votes within last day AND having vote sum > 0, latest updated first <- most recent updated is most trending
     * Defining popular as those having the favour of most => vote sum > num votes / 2 => 75%+ users liked it, <- most recent created first
     */
    @RequestMapping(value = "/feed", method = RequestMethod.GET)
    public List<SnippetListItem> getSnippetsForFeed() {
        List<SnippetListItem> snippetsForFeed = new ArrayList<>(snippetService.getSnippetsForFeed(getUserIdFromRequest()));
        snippetsForFeed.sort((o1, o2) -> {
            Timestamp time1;
            if (SnippetListItem.CATEGORY_NEW.equals(o1.getCategory())
                    || SnippetListItem.CATEGORY_POPULAR.equals(o1.getCategory())) {
                time1 = o1.getSnippetCreationTime();
            } else {
                time1 = o1.getSnippetStats().getUpdatedAt();
            }

            Timestamp time2;
            if (SnippetListItem.CATEGORY_NEW.equals(o2.getCategory())
                    || SnippetListItem.CATEGORY_POPULAR.equals(o2.getCategory())) {
                time2 = o2.getSnippetCreationTime();
            } else {
                time2 = o2.getSnippetStats().getUpdatedAt();
            }

            return time2.compareTo(time1);
        });
        return snippetsForFeed;
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public JsonElement addNewSnippet(@RequestBody Snippet snippet) {
        if (!snippet.getAuthorUser().getId().equals(getUserIdFromRequest())) {
            throw new IllegalArgumentException("invalid user");
        }
        Snippet addedSnippet = snippetService.addNewSnippet(snippet);
        return prepareResponseFrom(addedSnippet);
    }

    /**
     * Definition of New, Trending and Popular same as that for snippets
     */
    @RequestMapping(value = "/end/feed", method = RequestMethod.GET)
    public List<StoryListItem> getStoriesForFeed() {
        List<StoryListItem> snippetsForFeed = new ArrayList<>(snippetService.getStoriesForFeed(getUserIdFromRequest()));
        snippetsForFeed.sort((o1, o2) -> {
            Timestamp time1;
            if (SnippetListItem.CATEGORY_NEW.equals(o1.getCategory())
                    || SnippetListItem.CATEGORY_POPULAR.equals(o1.getCategory())) {
                time1 = o1.getSnippetCreationTime();
            } else {
                time1 = o1.getEndSnippetUpdateTime();
            }

            Timestamp time2;
            if (SnippetListItem.CATEGORY_NEW.equals(o2.getCategory())
                    || SnippetListItem.CATEGORY_POPULAR.equals(o2.getCategory())) {
                time2 = o2.getSnippetCreationTime();
            } else {
                time2 = o2.getEndSnippetUpdateTime();
            }

            return time2.compareTo(time1);
        });
        return snippetsForFeed;
    }

    @RequestMapping(value = "/end/", method = RequestMethod.POST)
    public JsonElement addNewEnd(@RequestBody Story story) {
        if (!story.getEndSnippet().getAuthorUser().getId().equals(getUserIdFromRequest())) {
            throw new IllegalArgumentException("invalid user");
        }
        Story addedStory = snippetService.addNewEnd(story);
        return prepareResponseFrom(addedStory);
    }

    @RequestMapping(value = "/vote/", method = RequestMethod.PUT, produces = "application/json")
    public UserSnippetVote addUserVoteForSnippet(@RequestBody UserSnippetVote userSnippetVote) {
        if (!userSnippetVote.getUser().getId().equals(getUserIdFromRequest())) {
            throw new IllegalArgumentException("invalid user");
        }
        return snippetService.addUserVote(userSnippetVote);
    }

    @RequestMapping(value = "/{id}/tree/", method = RequestMethod.GET, produces = "application/json")
    public List<SnippetListItem> getSnippetTreeFor(@PathVariable long id) {
        return snippetService.getSnippetTreeWithRootId(id, getUserIdFromRequest());
    }
}
