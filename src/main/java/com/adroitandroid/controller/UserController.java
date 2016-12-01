package com.adroitandroid.controller;

import com.adroitandroid.model.Chapter;
import com.adroitandroid.model.Notification;
import com.adroitandroid.model.service.ChapterService;
import com.adroitandroid.model.service.NotificationService;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
