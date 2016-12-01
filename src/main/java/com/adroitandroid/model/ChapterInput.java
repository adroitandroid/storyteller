package com.adroitandroid.model;

/**
 * Created by pv on 30/11/16.
 */
public class ChapterInput {
    public Long storyId;
    public Long previousChapterId;
    public String chapterTitle;
    public String chapterPlot;
    public Long userId;

    public ChapterInput(Long storySummaryId, Long prevChapterId, String chapterTitle, String chapterPlot, Long userId) {
        this.storyId = storySummaryId;
        this.previousChapterId = prevChapterId;
        this.chapterTitle = chapterTitle;
        this.chapterPlot = chapterPlot;
        this.userId = userId;
    }
}
