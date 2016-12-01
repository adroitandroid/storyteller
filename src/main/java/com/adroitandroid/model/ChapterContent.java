package com.adroitandroid.model;

/**
 * Created by pv on 01/12/16.
 */
public class ChapterContent {
    private Long chapterId;
    private String content;

    public ChapterContent(Long chapterId, String content) {
        this.chapterId = chapterId;
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public Long getChapterId() {
        return chapterId;
    }
}
