package com.adroitandroid.controller;

import com.adroitandroid.model.Story;
import com.adroitandroid.model.UserSnippetId;
import com.adroitandroid.model.UserSnippetRelation;
import com.adroitandroid.model.service.StoryService;
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
@RequestMapping(value = "/stories")
public class StoryController {

    @Autowired
    private UserSnippetRelationService userSnippetRelationService;
    @Autowired
    private StoryService storyService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public List<Story> getRecentBestStories() {
        return storyService.getRecentBest(new Timestamp((new Date()).getTime()));
    }

    @RequestMapping(value = "/likes/add", method = RequestMethod.POST)
    public HashMap<String, Integer> addStoryLike(@RequestBody UserSnippetId userSnippetId) {
        int success = userSnippetRelationService.insertOnDuplicateKeyUpdate(userSnippetId.getUserId(), userSnippetId.getSnippetId(),
                UserSnippetRelationService.LIKE, new Timestamp((new Date()).getTime()), false, Boolean.TRUE);
        return getSuccessStateResponse(success);
    }

    @RequestMapping(value = "/likes/remove", method = RequestMethod.PUT)
    public HashMap<String, Integer> removeStoryLike(@RequestBody UserSnippetId userSnippetId) {
        int success = userSnippetRelationService.insertOnDuplicateKeyUpdate(userSnippetId.getUserId(), userSnippetId.getSnippetId(),
                UserSnippetRelationService.LIKE, new Timestamp((new Date()).getTime()), true, Boolean.FALSE);
        return getSuccessStateResponse(success);
    }

    @RequestMapping(value = "/likes", method = RequestMethod.GET)
    public List<UserSnippetRelation> getAllUserLikes(@RequestParam(name = "user_id") Long userId) {
        return userSnippetRelationService.getAllRelationsForUser(userId, UserSnippetRelationService.LIKE);
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
