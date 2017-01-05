package com.adroitandroid.controller;

import com.adroitandroid.model.Snippet;
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

    @RequestMapping(value = "/feed", method = RequestMethod.GET)
    public List<Snippet> getSnippetsForFeed() {
        List<Snippet> trendingSnippets = snippetService.getTrendingSnippetsForFeed();
        List<Snippet> popularSnippets = snippetService.getPopularSnippetsForFeed();
        List<Snippet> newSnippets = snippetService.getNewSnippetsForFeed();
        List<Snippet> otherSnippets = snippetService.getNormalSnippetsForFeed();
        List<Snippet> snippetFeed = new ArrayList<>();
        snippetFeed.addAll(trendingSnippets);
        snippetFeed.addAll(popularSnippets);
        snippetFeed.addAll(newSnippets);
        snippetFeed.addAll(otherSnippets);
        return snippetFeed;
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public JsonElement addNewSnippet(@RequestBody Snippet snippet) {
        Snippet addedSnippet = snippetService.addNewSnippet(snippet);
        return prepareResponseFrom(addedSnippet);
    }
}
