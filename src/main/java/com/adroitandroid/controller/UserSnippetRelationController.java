package com.adroitandroid.controller;

import com.adroitandroid.model.UserSnippetId;
import com.adroitandroid.model.UserSnippetRelation;
import com.adroitandroid.model.service.UserSnippetRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by pv on 25/10/16.
 */
@RestController
@RequestMapping(value = "/")
public class UserSnippetRelationController {

//    TODO: these relation types are exclusive of each other, what can be liked cannot be bookmarked and vice versa, hence redundant for now
    private static final String BOOKMARK = "bookmark";
    private static final String LIKE = "like";

    @Autowired
    private UserSnippetRelationService userSnippetRelationService;

    @RequestMapping(value = "/bookmark/add", method = RequestMethod.POST)
    public HashMap<String, Integer> addSnippetBookmark(@RequestBody UserSnippetId userSnippetId) {
        int success = userSnippetRelationService.insertOnDuplicateKeyUpdate(userSnippetId.getUserId(), userSnippetId.getSnippetId(),
                BOOKMARK, new Timestamp((new Date()).getTime()), false, null);
        return getSuccessStateResponse(success);
    }

    @RequestMapping(value = "/like/add", method = RequestMethod.POST)
    public HashMap<String, Integer> addStoryLike(@RequestBody UserSnippetId userSnippetId) {
        int success = userSnippetRelationService.insertOnDuplicateKeyUpdate(userSnippetId.getUserId(), userSnippetId.getSnippetId(),
                LIKE, new Timestamp((new Date()).getTime()), false, Boolean.TRUE);
        return getSuccessStateResponse(success);
    }

    @RequestMapping(value = "/bookmark/remove", method = RequestMethod.PUT)
    public HashMap<String, Integer> removeSnippetBookmark(@RequestBody UserSnippetId userSnippetId) {
        int success = userSnippetRelationService.insertOnDuplicateKeyUpdate(userSnippetId.getUserId(), userSnippetId.getSnippetId(),
                BOOKMARK, new Timestamp((new Date()).getTime()), true, null);
        return getSuccessStateResponse(success);
    }

    @RequestMapping(value = "/like/remove", method = RequestMethod.PUT)
    public HashMap<String, Integer> removeStoryLike(@RequestBody UserSnippetId userSnippetId) {
        int success = userSnippetRelationService.insertOnDuplicateKeyUpdate(userSnippetId.getUserId(), userSnippetId.getSnippetId(),
                LIKE, new Timestamp((new Date()).getTime()), true, Boolean.FALSE);
        return getSuccessStateResponse(success);
    }

    @RequestMapping(value = "/bookmark", method = RequestMethod.GET)
    public List<UserSnippetRelation> getAllUserBookmarks(@RequestParam(name = "user_id") Long userId) {
        return userSnippetRelationService.getAllRelationsForUser(userId, BOOKMARK);
    }

    @RequestMapping(value = "/like", method = RequestMethod.GET)
    public List<UserSnippetRelation> getAllUserLikes(@RequestParam(name = "user_id") Long userId) {
        return userSnippetRelationService.getAllRelationsForUser(userId, LIKE);
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
