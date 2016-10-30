package com.adroitandroid.controller;

import com.adroitandroid.model.UserSnippetId;
import com.adroitandroid.model.service.StorySnippetService;
import com.adroitandroid.model.service.UserSnippetRelationService;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by pv on 25/10/16.
 */
@RestController
@RequestMapping(value = "/bookmarks")
public class UserBookmarkController extends AbstractController {

    @Autowired
    private UserSnippetRelationService userSnippetRelationService;
    @Autowired
    private StorySnippetService storySnippetService;

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public HashMap<String, Integer> addSnippetBookmark(@RequestBody UserSnippetId userSnippetId) {
        int success = userSnippetRelationService.insertOnDuplicateKeyUpdate(userSnippetId.getUserId(), userSnippetId.getSnippetId(),
                UserSnippetRelationService.BOOKMARK, new Timestamp((new Date()).getTime()), false, null);
        return getSuccessStateResponse(success);
    }

    @RequestMapping(value = "/remove", method = RequestMethod.PUT)
    public HashMap<String, Integer> removeSnippetBookmark(@RequestBody UserSnippetId userSnippetId) {
        int success = userSnippetRelationService.insertOnDuplicateKeyUpdate(userSnippetId.getUserId(), userSnippetId.getSnippetId(),
                UserSnippetRelationService.BOOKMARK, new Timestamp((new Date()).getTime()), true, null);
        return getSuccessStateResponse(success);
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ArrayNode getAllBookmarksForUser(@RequestParam(name = "active", required = false, defaultValue = "true") boolean activePrompts) {
        return storySnippetService.getAllSnippetsRelatedToUser(getUserIdFromRequest(), activePrompts, UserSnippetRelationService.BOOKMARK);
    }

    private HashMap<String, Integer> getSuccessStateResponse(int success) {
        HashMap<String, Integer> map = new HashMap<>();
        map.put("success", success);
        return map;
    }

    @ExceptionHandler
    void handleIllegalArgumentException(IllegalArgumentException e, HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value());
    }
}
