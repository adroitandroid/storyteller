package com.adroitandroid.controller;

import com.adroitandroid.model.*;
import com.adroitandroid.model.service.ChapterService;
import com.adroitandroid.model.service.NotificationService;
import com.adroitandroid.model.service.UserService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
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
     * @return
     */
    @RequestMapping(value = "/sign_in", method = RequestMethod.POST)
    public CompletableFuture<UserLoginDetails> signInUser(@RequestBody UserLoginInfo userLoginInfo)
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

    @RequestMapping(value = "/token", method = RequestMethod.PUT)
    public JsonObject updateUserDetailsToken(@RequestBody UserDetailInfo userDetailInfo) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("success", userService.updateToken(getUserIdFromRequest(), userDetailInfo.fcmToken));
        return jsonObject;
    }

    @RequestMapping(value = "/message", method = RequestMethod.GET, produces = "application/json")
    public JsonObject isAnyUnreadMessage() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("unreads", notificationService.anyUnreadNotificationForUserId(needUserId()));
        return jsonObject;
    }

    @RequestMapping(value = "/message/list", method = RequestMethod.GET, produces = "application/json")
    public List<Message> getAllMessagesFor() {
        JsonElement jsonElement = prepareResponseFrom(notificationService.getUnreadSortedByEdfAndReadSortedByLifoFor(needUserId()),
                Notification.RECEIVER_CHAPTER, Notification.SENDER_CHAPTER, Chapter.STORY_SUMMARY, Notification.SENDER_USER);
        Notification[] notifications = new Gson().fromJson(jsonElement, Notification[].class);
        List<Message> messageList = new ArrayList<>();
        for (Notification notification : notifications) {
            messageList.add(new Message(notification));
        }
        return messageList;
    }

    @RequestMapping(value = "/drafts", method = RequestMethod.GET, produces = "application/json")
    public JsonElement getAllDraftsFor() {
        return prepareResponseFrom(chapterService.findAllChaptersByAuthorIdWithStatus(needUserId(), true,
                Chapter.STATUS_APPROVED, Chapter.STATUS_AUTO_APPROVED), Chapter.STORY_SUMMARY);
    }

    @RequestMapping(value = "/published", method = RequestMethod.GET, produces = "application/json")
    public JsonElement getAllPublishedFor() {
        return prepareResponseFrom(chapterService.findAllChaptersByAuthorIdWithStatus(needUserId(), true,
                Chapter.STATUS_PUBLISHED), Chapter.STORY_SUMMARY, Chapter.CHAPTER_STATS);
    }

    @RequestMapping(value = "/set_username", method = RequestMethod.PUT, produces = "application/json")
    public JsonObject setUsername(@RequestBody UserLoginDetails userDetails) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("success", userService.setUsername(getUserIdFromRequest(), userDetails.username));
        return jsonObject;
    }

    @RequestMapping(value = "/like", method = RequestMethod.POST, produces = "application/json")
    public JsonObject likeStory(@RequestBody StoryId storyId) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("success", userService.setLiked(needUserId(), storyId.getStoryId()));
        return jsonObject;
    }

    @RequestMapping(value = "/like", method = RequestMethod.DELETE, produces = "application/json")
    public JsonObject unlikeStory(@RequestBody StoryId storyId) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("success", userService.unsetLiked(needUserId(), storyId.getStoryId()));
        return jsonObject;
    }

    @RequestMapping(value = "/like", method = RequestMethod.GET, produces = "application/json")
    public JsonElement getLikes() {
        JsonElement jsonElement = prepareResponseFrom(
                userService.getUserLikesSortedByRecentFirst(needUserId()), UserStoryRelation.STORY_SUMMARY,
                StorySummary.PROMPT, StorySummary.STORY_STATS, StorySummary.STORY_GENRES, StoryGenre.GENRE);
        return getStoriesJsonArrayFromUserStoryRelations(jsonElement);
    }

    @RequestMapping(value = "/read_later", method = RequestMethod.POST, produces = "application/json")
    public JsonObject readStoryLater(@RequestBody StoryId storyId) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("success", userService.setToReadLater(needUserId(), storyId.getStoryId()));
        return jsonObject;
    }

    @RequestMapping(value = "/read_later", method = RequestMethod.DELETE, produces = "application/json")
    public JsonObject removeStoryFromReadLater(@RequestBody StoryId storyId) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("success", userService.removeFromReadLater(needUserId(), storyId.getStoryId()));
        return jsonObject;
    }

    @RequestMapping(value = "/read_later", method = RequestMethod.GET, produces = "application/json")
    public JsonElement getStoriesToReadLater() {
        JsonElement jsonElement = prepareResponseFrom(userService.getUserReadLaterSortedByRecentFirst(needUserId()),
                UserStoryRelation.STORY_SUMMARY, StorySummary.PROMPT, StorySummary.STORY_STATS,
                StorySummary.STORY_GENRES, StoryGenre.GENRE);
        return getStoriesJsonArrayFromUserStoryRelations(jsonElement);
    }

    @RequestMapping(value = "/bookmark", method = RequestMethod.POST, produces = "application/json")
    public JsonObject bookmarkChapter(@RequestBody ChapterId chapterId) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("success", userService.setBookmark(needUserId(), chapterId.getChapterId()));
        return jsonObject;
    }

    @RequestMapping(value = "/bookmark", method = RequestMethod.DELETE, produces = "application/json")
    public JsonObject unbookmarkChapter(@RequestBody ChapterId chapterId) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("success", userService.removeBookmark(needUserId(), chapterId.getChapterId()));
        return jsonObject;
    }

    @RequestMapping(value = "/bookmark", method = RequestMethod.GET, produces = "application/json")
    public JsonElement getBookmarks() {
        JsonElement jsonElement = prepareResponseFrom(userService.getUserBookmarksSortedByRecentFirst(needUserId()),
                UserChapterRelation.CHAPTER_SUMMARY, Chapter.STORY_SUMMARY);
        UserChapterRelation[] userChapterRelations = new Gson().fromJson(jsonElement, UserChapterRelation[].class);
        JsonArray chapters = new JsonArray();
        for (UserChapterRelation relation : userChapterRelations) {
            chapters.add(new Gson().toJsonTree(relation.getChapter()));
        }
        return chapters;
    }

    private JsonElement getStoriesJsonArrayFromUserStoryRelations(JsonElement jsonElement) {
        UserStoryRelation[] userStoryRelations = new Gson().fromJson(jsonElement, UserStoryRelation[].class);
        JsonArray stories = new JsonArray();
        for (UserStoryRelation relation : userStoryRelations) {
            stories.add(new Gson().toJsonTree(relation.story));
        }
        return stories;
    }

    //#####################TODO: vver starts from here #######################
    @RequestMapping(value = "/bookmark/", method = RequestMethod.PUT, produces = "application/json")
    public void updateUserBookmark(@RequestBody UserBookmark userBookmark) {
        userService.updateUserBookmark(userBookmark);
    }

    /**
     * An update is defined as an activity of interest - a change of votes or a contribution on top of the snippet
     * SINCE last login time OR within last day, whichever is earlier
     */
    @RequestMapping(value = "/updates/", method = RequestMethod.GET, produces = "application/json")
    public List<SnippetListItemForUpdate> getUpdatesFor(@RequestParam(value = "user_id") Long userId) {
        return userService.getUpdatesFor(userId);
    }
}
