package com.adroitandroid.model.service;

import com.adroitandroid.model.Chapter;
import com.adroitandroid.model.ChapterContent;
import com.adroitandroid.model.ChapterDetail;
import com.adroitandroid.model.ChapterInput;

/**
 * Created by pv on 30/11/16.
 */
public interface ChapterService {
    void validateAddChapterInput(ChapterInput input);

    Chapter addChapter(ChapterInput chapterInput);

    void addChapterApproval(Long chapterId, Long notificationId, Boolean approval);

    void editChapter(ChapterDetail chapterDetail);

    ChapterDetail addContent(ChapterContent chapterContent);
}
