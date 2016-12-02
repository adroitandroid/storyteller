package com.adroitandroid.controller;

import com.adroitandroid.model.*;
import com.adroitandroid.model.service.ChapterService;
import com.adroitandroid.model.service.NotificationService;
import com.adroitandroid.model.service.UserService;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.CompletableFuture;

/**
 * Created by pv on 01/12/16.
 */
@RestController
@RequestMapping(value = "/user")
public class UserController extends AbstractController {

    @Autowired
    private NotificationService notificationService;
    @Autowired
    private ChapterService chapterService;
    @Autowired
    private UserService userService;

    /**
     * Called by new user as well as any returning user whose session has expired
     * @param userLoginInfo
     * @return
     */
    @RequestMapping(value = "/sign_in", method = RequestMethod.POST)
    public CompletableFuture<UserDetails> signInUser(@RequestBody UserLoginInfo userLoginInfo)
            throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {
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

    @RequestMapping(value = "/message", method = RequestMethod.GET, produces = "application/json")
    public JsonObject isAnyUnreadMessage() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("unreads", notificationService.anyUnreadNotificationForUserId(getUserIdFromRequest()));
        return jsonObject;
    }

    @RequestMapping(value = "/message/list", method = RequestMethod.GET, produces = "application/json")
    public JsonElement getAllMessagesFor() {
        return prepareResponseFrom(notificationService.getUnreadSortedByEdfAndReadSortedByMruFor(getUserIdFromRequest()),
                Notification.RECEIVER_CHAPTER, Notification.SENDER_CHAPTER, Notification.SENDER_USER);
    }

    @RequestMapping(value = "/drafts", method = RequestMethod.GET, produces = "application/json")
    public JsonElement getAllDraftsFor() {
        return prepareResponseFrom(chapterService.findAllChaptersByAuthorIdWithStatus(getUserIdFromRequest(), true,
                Chapter.STATUS_APPROVED, Chapter.STATUS_AUTO_APPROVED), Chapter.STORY_SUMMARY);
    }

    @RequestMapping(value = "/published", method = RequestMethod.GET, produces = "application/json")
    public JsonElement getAllPublishedFor() {
        return prepareResponseFrom(chapterService.findAllChaptersByAuthorIdWithStatus(getUserIdFromRequest(), true,
                Chapter.STATUS_PUBLISHED), Chapter.STORY_SUMMARY);
    }

    @RequestMapping(value = "/set_username", method = RequestMethod.PUT, produces = "application/json")
    public JsonObject setUsername(@RequestBody User user) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("success", userService.setUsername(user.getId(), user.username));
        return jsonObject;
    }

    @RequestMapping(value = "/like", method = RequestMethod.POST, produces = "application/json")
    public JsonObject likeStory(@RequestBody UserStoryPair userStoryPair) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("success", userService.setLiked(userStoryPair.getUserId(), userStoryPair.getStoryId()));
        return jsonObject;
    }

    @RequestMapping(value = "/like", method = RequestMethod.DELETE, produces = "application/json")
    public JsonObject unlikeStory(@RequestBody UserStoryPair userStoryPair) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("success", userService.unsetLiked(userStoryPair.getUserId(), userStoryPair.getStoryId()));
        return jsonObject;
    }

    @RequestMapping(value = "/like", method = RequestMethod.GET, produces = "application/json")
    public JsonElement getLikes() {
        return prepareResponseFrom(
                userService.getUserLikesSortedByRecentFirst(getUserIdFromRequest()), UserStoryRelation.STORY_SUMMARY,
                StorySummary.PROMPT, StorySummary.STORY_STATS, StorySummary.STORY_GENRES, StoryGenre.GENRE);
    }

    @RequestMapping(value = "/read_later", method = RequestMethod.POST, produces = "application/json")
    public JsonObject readStoryLater(@RequestBody UserStoryPair userStoryPair) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("success", userService.setToReadLater(userStoryPair.getUserId(), userStoryPair.getStoryId()));
        return jsonObject;
    }

    @RequestMapping(value = "/read_later", method = RequestMethod.DELETE, produces = "application/json")
    public JsonObject removeStoryFromReadLater(@RequestBody UserStoryPair userStoryPair) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("success", userService.removeFromReadLater(userStoryPair.getUserId(), userStoryPair.getStoryId()));
        return jsonObject;
    }

    @RequestMapping(value = "/read_later", method = RequestMethod.GET, produces = "application/json")
    public JsonElement getStoriesToReadLater() {
        return prepareResponseFrom(userService.getUserReadLaterSortedByRecentFirst(getUserIdFromRequest()),
                UserStoryRelation.STORY_SUMMARY, StorySummary.PROMPT, StorySummary.STORY_STATS,
                StorySummary.STORY_GENRES, StoryGenre.GENRE);
    }

    @RequestMapping(value = "/bookmark", method = RequestMethod.POST, produces = "application/json")
    public JsonObject bookmarkChapter(@RequestBody UserChapterPair userChapterPair) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("success", userService.setBookmark(userChapterPair.getUserId(), userChapterPair.getChapterId()));
        return jsonObject;
    }

    @RequestMapping(value = "/bookmark", method = RequestMethod.DELETE, produces = "application/json")
    public JsonObject unbookmarkChapter(@RequestBody UserChapterPair userChapterPair) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("success", userService.removeBookmark(userChapterPair.getUserId(), userChapterPair.getChapterId()));
        return jsonObject;
    }

    @RequestMapping(value = "/bookmark", method = RequestMethod.GET, produces = "application/json")
    public JsonElement getBookmarks() {
        return prepareResponseFrom(userService.getUserBookmarksSortedByRecentFirst(getUserIdFromRequest()),
                UserChapterRelation.CHAPTER_SUMMARY, Chapter.STORY_SUMMARY);
    }
}
