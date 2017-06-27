package com.adroitandroid.controller;

import com.adroitandroid.model.*;
import com.adroitandroid.model.service.UserService;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Created by pv on 01/12/16.
 */
@RestController
@RequestMapping(value = "/user")
public class UserController extends AbstractController {

    @Autowired
    private UserService userService;

    /**
     * Called by new user as well as any returning user whose session has expired
     * @return
     */
    @RequestMapping(value = "/sign_in", method = RequestMethod.POST)
    public CompletableFuture<UserLoginDetails> signInUser(@RequestBody UserLoginInfo userLoginInfo)
            throws GeneralSecurityException, IOException, InterruptedException {
        validateRequest(userLoginInfo);
        return userService.signIn(userLoginInfo);
    }

    private void validateRequest(UserLoginInfo userLoginInfo) {
        if (userLoginInfo.getAuthUserId() == null
                || userLoginInfo.getAccessToken() == null
                || userLoginInfo.getAuthenticationType() == null) {
            throw new IllegalArgumentException("incomplete login details");
        }
    }

    @RequestMapping(value = "/token", method = RequestMethod.PUT)
    public JsonObject updateUserDetailsToken(@RequestBody UserDetailInfo userDetailInfo) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("success", userService.updateToken(getUserIdFromRequest(), userDetailInfo.fcmToken));
        return jsonObject;
    }

    @RequestMapping(value = "/bookmark/", method = RequestMethod.PUT, produces = "application/json")
    public void updateUserBookmark(@RequestBody UserBookmark userBookmark) {
        Long userIdFromRequest = getUserIdFromRequest();
        if (userIdFromRequest < 0) {
            throw new IllegalArgumentException("invalid user");
        }
        if (!userBookmark.getUserId().equals(userIdFromRequest)) {
            throw new IllegalArgumentException("invalid user");
        }
        userService.updateUserBookmark(userBookmark);
    }

    @RequestMapping(value = "/bookmark/all/", method = RequestMethod.GET, produces = "application/json")
    public List<SnippetListItem> updateUserBookmark() {
        Long userIdFromRequest = getUserIdFromRequest();
        if (userIdFromRequest < 0) {
            throw new IllegalArgumentException("invalid user");
        }
        return userService.getAllBookmarksOf(userIdFromRequest);
    }

    /**
     * An update is defined as an activity of interest -
     * 1. a change of votes or a contribution on top of contributed or bookmarked snippet OR
     * 2. a new contribution from someone followed
     * SINCE last login time OR within last day, whichever is earlier
     */
    @RequestMapping(value = "/updates/", method = RequestMethod.GET, produces = "application/json")
    public List<SnippetListItemForUpdate> getUpdatesFor() {
        Long userIdFromRequest = getUserIdFromRequest();
        if (userIdFromRequest < 0) {
            throw new IllegalArgumentException("invalid user");
        }
        return userService.getUpdatesFor(userIdFromRequest);
    }

    @RequestMapping(value = "/status/", method = RequestMethod.GET, produces = "application/json")
    public List<UserStatus> getStatusFor() {
        Long userIdFromRequest = getUserIdFromRequest();
        if (userIdFromRequest < 0) {
            throw new IllegalArgumentException("invalid user");
        }
        return userService.getStatusFor(userIdFromRequest);
    }

    @RequestMapping(value = "/status/", method = RequestMethod.PUT, produces = "application/json")
    public void updateStatusFor(@RequestBody UserStatus userStatus) {
        Long userIdFromRequest = getUserIdFromRequest();
        if (userIdFromRequest < 0) {
            throw new IllegalArgumentException("invalid user");
        }
        if (!userStatus.getUserId().equals(userIdFromRequest)) {
            throw new IllegalArgumentException("invalid user");
        }
        userService.updateStatus(userStatus);
    }

    @RequestMapping(value = "/follow/", method = RequestMethod.PUT, produces = "application/json")
    public void updateFollowed(@RequestBody UserRelation userRelation) {
        Long userIdFromRequest = getUserIdFromRequest();
        if (userIdFromRequest < 0) {
            throw new IllegalArgumentException("invalid user");
        }
        if (!userRelation.getFollowerUserId().equals(userIdFromRequest)) {
            throw new IllegalArgumentException("invalid user");
        }
        userService.updateFollowRelationship(
                userRelation.getUserId(), userRelation.getFollowerUserId(), userRelation.isSoftDeleted());
    }

    @RequestMapping(value = "/profile/", method = RequestMethod.GET, produces = "application/json")
    public UserProfile getProfileFor(@RequestParam(value = "user_id") Long userId) {
        Long userIdFromRequest = getUserIdFromRequest();
        if (userIdFromRequest < 0) {
            throw new IllegalArgumentException("invalid user");
        }
        return userService.getProfileFor(userId, userIdFromRequest);
    }

    /**
     * Change in name or description of user
     */
    @RequestMapping(value = "/profile/", method = RequestMethod.PUT, produces = "application/json")
    public void updateProfile(@RequestBody User user) {
        Long userIdFromRequest = getUserIdFromRequest();
        if (userIdFromRequest < 0) {
            throw new IllegalArgumentException("invalid user");
        }
        if (!user.getId().equals(userIdFromRequest)) {
            throw new IllegalArgumentException("invalid user");
        }
        userService.update(user);
    }
}
