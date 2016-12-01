package com.adroitandroid.controller;

import com.adroitandroid.model.*;
import com.adroitandroid.model.service.ChapterService;
import com.adroitandroid.model.service.NotificationService;
import com.adroitandroid.model.service.UserService;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @RequestMapping(value = "/message", method = RequestMethod.GET, produces = "application/json")
    public JsonObject isAnyUnreadMessage(@RequestParam(value = "user_id") Long userId) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("unreads", notificationService.anyUnreadNotificationForUserId(userId));
        return jsonObject;
    }

    @RequestMapping(value = "/message/list", method = RequestMethod.GET, produces = "application/json")
    public JsonElement getAllMessagesFor(@RequestParam(value = "user_id") Long userId) {
        return prepareResponseFrom(notificationService.getUnreadSortedByEdfAndReadSortedByMruFor(userId),
                Notification.RECEIVER_CHAPTER, Notification.SENDER_CHAPTER, Notification.SENDER_USER);
    }

    @RequestMapping(value = "/drafts", method = RequestMethod.GET, produces = "application/json")
    public JsonElement getAllDraftsFor(@RequestParam(value = "user_id") Long userId) {
        return prepareResponseFrom(chapterService.findAllChaptersByAuthorIdWithStatus(userId, true,
                Chapter.STATUS_APPROVED, Chapter.STATUS_AUTO_APPROVED), Chapter.STORY_SUMMARY);
    }

    @RequestMapping(value = "/published", method = RequestMethod.GET, produces = "application/json")
    public JsonElement getAllPublishedFor(@RequestParam(value = "user_id") Long userId) {
        return prepareResponseFrom(chapterService.findAllChaptersByAuthorIdWithStatus(userId, true,
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
    public JsonElement getLikes(@RequestParam(value = "user_id") Long userId) {
        return prepareResponseFrom(userService.getUserLikesSortedByRecentFirst(userId), UserStoryRelation.STORY_SUMMARY,
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
    public JsonElement getStoriesToReadLater(@RequestParam(value = "user_id") Long userId) {
        return prepareResponseFrom(userService.getUserReadLaterSortedByRecentFirst(userId), UserStoryRelation.STORY_SUMMARY,
                StorySummary.PROMPT, StorySummary.STORY_STATS, StorySummary.STORY_GENRES, StoryGenre.GENRE);
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
    public JsonElement getBookmarks(@RequestParam(value = "user_id") Long userId) {
        return prepareResponseFrom(userService.getUserBookmarksSortedByRecentFirst(userId), UserChapterRelation.CHAPTER_SUMMARY, Chapter.STORY_SUMMARY);
    }
}
