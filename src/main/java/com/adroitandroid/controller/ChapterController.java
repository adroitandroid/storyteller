package com.adroitandroid.controller;

import com.adroitandroid.model.*;
import com.adroitandroid.model.service.ChapterService;
import com.adroitandroid.model.service.StoryService;
import com.google.gson.JsonElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by pv on 30/11/16.
 */
@RestController
@RequestMapping(value = "/chapter")
public class ChapterController extends ChapterCreateUpdateController {
    @Autowired
    private ChapterService chapterService;
    @Autowired
    private StoryService storyService;

    /**
     * Called when proposing a new chapter to the current chapter's author
     * @param chapterInput
     * @return
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public JsonElement addNewChapter(@RequestBody ChapterInput chapterInput) {
        Chapter chapter = validateAndAddNewChapter(chapterInput, true);
        return prepareResponseFrom(chapter);
    }

    /**
     * Called when current author approves the next chapter
     * @param chapterApproval
     */
    @RequestMapping(value = "/approve", method = RequestMethod.PATCH)
    public void approveChapter(@RequestBody ChapterApproval chapterApproval) {
        chapterService.addChapterApproval(chapterApproval.chapterId, chapterApproval.notificationId, chapterApproval.approval);
    }

    /**
     * Called when new chapter's content is saved as draft for the first time
     * @param chapterContent
     * @return
     */
    @RequestMapping(value = "/add_content", method = RequestMethod.PUT)
    public JsonElement addContent(@RequestBody ChapterContent chapterContent) {
        return prepareResponseFrom(addChapterContent(chapterContent));
    }

    /**
     * Called whenever new chapter's content is saved as draft and chapter detail id is known
     * @param chapterDetail
     */
    @RequestMapping(value = "/edit_content", method = RequestMethod.PATCH)
    public void editContent(@RequestBody ChapterDetail chapterDetail) {
        chapterService.editChapter(chapterDetail);
    }

    /**
     * Called when publish is clicked instead of save as draft, could be a new chapter detail
     * @param contentToPublish
     */
    @RequestMapping(value = "/publish", method = RequestMethod.PATCH)
    public void publish(@RequestBody ChapterContentToPublish contentToPublish) {
        publishChapter(contentToPublish);
    }

    @Override
    StoryService getStoryService() {
        return storyService;
    }

    @Override
    ChapterService getChapterService() {
        return chapterService;
    }
}
