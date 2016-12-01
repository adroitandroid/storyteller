package com.adroitandroid.model.service;

import com.adroitandroid.model.*;

import java.util.List;

/**
 * Created by pv on 30/11/16.
 */
public interface ChapterService {
    void validateAddChapterInput(ChapterInput input);

    Chapter addChapter(ChapterInput chapterInput);

    void addChapterApproval(Long chapterId, Long notificationId, Boolean approval);

    void editChapter(ChapterDetail chapterDetail);

    ChapterDetail addContent(ChapterContent chapterContent);

    Chapter validatePublishChapterInput(ChapterContentToPublish contentToPublish);

    void updateSummaryAndChapterGenres(Chapter chapter, boolean endsStory, int statusPublished, List<String> genreNames);
}
