package com.adroitandroid.controller;

import com.adroitandroid.model.*;
import com.adroitandroid.model.service.ChapterService;
import com.adroitandroid.model.service.StoryService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by pv on 01/12/16.
 */
@RestController
abstract class ChapterCreateUpdateController extends AbstractController {

    Chapter validateAndAddNewChapter(@RequestBody ChapterInput chapterInput, boolean prevChapterPresent) {
        getChapterService().validateAddChapterInput(chapterInput, prevChapterPresent);
        return getChapterService().addChapter(chapterInput);
    }

    ChapterDetail addChapterContent(@RequestBody ChapterContent chapterContent) {
        return getChapterService().addContent(chapterContent);
    }

    void publishChapter(@RequestBody ChapterContentToPublish contentToPublish) {
        Chapter chapter = getChapterService().validatePublishChapterInput(contentToPublish);
        ChapterDetail chapterDetail = chapter.getDetail();
        if (chapterDetail == null) {
            addChapterContent(new ChapterContent(contentToPublish.getChapterId(), contentToPublish.getContent()));
            getChapterService().markNotificationReadForReceiverChapter(chapter);
        } else {
            chapterDetail.setContent(contentToPublish.getContent());
            getChapterService().editChapter(chapterDetail);
        }
        Chapter chapterFromDb = getChapterService().updateSummaryAndGenresForChapterAndStory(
                chapter, contentToPublish.isEndsStory(), Chapter.STATUS_PUBLISHED, contentToPublish.getGenreNames());
        if (chapterFromDb != null) { //implies that the chapter completed the story
            getStoryService().incrementStoryCompletedCount(chapterFromDb.getStorySummary().getId());
        }
    }

    abstract StoryService getStoryService();

    abstract ChapterService getChapterService();
}
