package com.adroitandroid.controller;

import com.adroitandroid.model.StorySnippet;
import com.adroitandroid.model.UserSnippetVote;
import com.adroitandroid.model.service.StorySnippetService;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by pv on 25/10/16.
 */
@RestController
@RequestMapping(value = "/snippets")
public class StorySnippetController {

    @Autowired
    private StorySnippetService storySnippetService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ArrayNode getAllSnippetsForPrompt(@RequestParam(name = "prompt_id", required = false) Long promptId,
                                             @RequestParam(name = "user_id", required = false) Long userId,
                                             @RequestParam(name = "active", required = false, defaultValue = "true") boolean activePrompts) {
        if (promptId != null) {
            return storySnippetService.getAllSnippetsForPrompt(promptId);
        } else if (userId != null) {
            return storySnippetService.getAllSnippetsByUser(userId, activePrompts);
        } else {
            throw new IllegalArgumentException("prompt_id or user_id need to be specified");
        }
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public StorySnippet addSnippet(@RequestBody StorySnippet snippet) {
        return storySnippetService.addSnippet(snippet);
    }

    @RequestMapping(value = "/vote", method = RequestMethod.PUT)
    public void addUserVoteForSnippet(@RequestBody UserSnippetVote vote) {
        storySnippetService.addUserVote(vote);
    }

    @ExceptionHandler
    void handleIllegalArgumentException(IllegalArgumentException e, HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value());
    }
}
