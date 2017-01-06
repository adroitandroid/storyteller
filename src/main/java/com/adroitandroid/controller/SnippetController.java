package com.adroitandroid.controller;

import com.adroitandroid.model.Snippet;
import com.adroitandroid.model.SnippetListItem;
import com.adroitandroid.model.UserSnippetVote;
import com.adroitandroid.model.service.SnippetService;
import com.google.gson.JsonElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
    public List<SnippetListItem> getSnippetsForNewFeed() {
        List<SnippetListItem> snippetsForFeed = new ArrayList<>(snippetService.getSnippetsForFeed());
        snippetsForFeed.sort((o1, o2) -> {
            if (SnippetListItem.CATEGORY_NEW.equals(o1.getCategory())
                    && SnippetListItem.CATEGORY_NEW.equals(o2.getCategory())) {
                return o1.getSnippetCreationTime().compareTo(o2.getSnippetCreationTime());
            }
            if (SnippetListItem.CATEGORY_POPULAR.equals(o1.getCategory())
                    && SnippetListItem.CATEGORY_POPULAR.equals(o2.getCategory())) {
                return o2.getSnippetCreationTime().compareTo(o1.getSnippetCreationTime());
            }
            return o2.getSnippetStats().getUpdatedAt().compareTo(o1.getSnippetStats().getUpdatedAt());
        });
        return snippetsForFeed;
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public JsonElement addNewSnippet(@RequestBody Snippet snippet) {
        Snippet addedSnippet = snippetService.addNewSnippet(snippet);
        return prepareResponseFrom(addedSnippet);
    }

    @RequestMapping(value = "/vote/", method = RequestMethod.PUT, produces = "application/json")
    public UserSnippetVote addUserVoteForSnippet(@RequestBody UserSnippetVote userSnippetVote) {
        return snippetService.addUserVote(userSnippetVote);
    }
}
