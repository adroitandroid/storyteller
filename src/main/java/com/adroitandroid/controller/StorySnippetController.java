package com.adroitandroid.controller;

import com.adroitandroid.model.StorySnippet;
import com.adroitandroid.model.UserSnippetVote;
import com.adroitandroid.model.service.StorySnippetService;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by pv on 25/10/16.
 */
@RestController
@RequestMapping(value = "/snippets")
public class StorySnippetController {

    @Autowired
    private StorySnippetService storySnippetService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ArrayNode getAllSnippetsForPrompt(@RequestParam(name = "prompt_id") long promptId) {
        return storySnippetService.getAllSnippetsForPrompt(promptId);
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public StorySnippet addSnippet(@RequestBody StorySnippet snippet) {
        return storySnippetService.addSnippet(snippet);
    }

    @RequestMapping(value = "/vote", method = RequestMethod.PUT)
    public void addUserVoteForSnippet(@RequestBody UserSnippetVote vote) {
        storySnippetService.addUserVote(vote);
    }
}
