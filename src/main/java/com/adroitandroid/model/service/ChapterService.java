package com.adroitandroid.model.service;

import com.adroitandroid.model.*;

import java.util.List;

/**
 * Created by pv on 30/11/16.
 */
public interface ChapterService {
    void validateAddChapterInput(ChapterInput input, boolean prevChapterRequired);

    Chapter addChapter(ChapterInput chapterInput);

    void addChapterApproval(Long chapterId, Long notificationId, Boolean approval);

    void editChapter(ChapterDetail chapterDetail);

    ChapterDetail addContent(ChapterContent chapterContent);

    Chapter validatePublishChapterInput(ChapterContentToPublish contentToPublish);

    Chapter updateSummaryAndGenresForChapterAndStory(Chapter chapter, boolean endsStory, int statusPublished, List<String> genreNames);

    void markNotificationReadForReceiverChapter(Chapter chapter);

    List<Chapter> findAllChaptersByAuthorIdWithStatus(Long userId, boolean includeSoftDeleted, Integer... status);
}
