package com.adroitandroid.model;

import java.util.List;

/**
 * Created by pv on 01/12/16.
 */
public class ChapterContentToPublish {
    private Long chapterId;
    private String content;
    private Boolean endsStory;
    private List<String> genreNames;

    public ChapterContentToPublish(Long chapterId, String content, Boolean endsStory, List<String> genreNames) {
        this.chapterId = chapterId;
        this.content = content;
        this.endsStory = endsStory;
        this.genreNames = genreNames;
    }

    public String getContent() {
        return content;
    }

    public List<String> getGenreNames() {
        return genreNames;
    }

    public Long getChapterId() {
        return chapterId;
    }

    public boolean isEndsStory() {
        return endsStory != null && endsStory;
    }
}
