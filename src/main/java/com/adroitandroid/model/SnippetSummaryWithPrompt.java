package com.adroitandroid.model;

import java.io.Serializable;
import java.util.Date;
import java.sql.Timestamp;

/**
 * Created by pv on 28/10/16.
 */
public class SnippetSummaryWithPrompt implements Serializable {
    private final StoryPrompt storyPrompt;
    private final SnippetSummary snippetSummary;

    public SnippetSummaryWithPrompt(Long id, Long parentId, String snippetText, Boolean isEnd, Long voteSum, Long voteCount,
                                    Long promptId, String prompt, Date promptStartDate, Date promptEndDate,
                                    Date promptCreateTime, Date promptUpdateTime) {
        this.snippetSummary = new SnippetSummary(id, parentId, snippetText, isEnd, voteSum, voteCount);
        this.storyPrompt = new StoryPrompt(promptId, prompt, promptStartDate, promptEndDate,
                new Timestamp(promptCreateTime.getTime()), new Timestamp(promptUpdateTime.getTime()), false);
    }

    public StoryPrompt getStoryPrompt() {
        return storyPrompt;
    }

    public SnippetSummary getSnippetSummary() {
        return snippetSummary;
    }
}
