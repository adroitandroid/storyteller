package com.adroitandroid.model.service;

import com.adroitandroid.model.StorySummary;
import com.adroitandroid.model.StoryWithChapterInput;

import java.util.List;

/**
 * Created by pv on 30/11/16.
 */
public interface StoryService {
    List<StorySummary> getAllCompletedStories();

    StorySummary getCompleteStoryById(long id);

    void incrementStoryCompletedCount(Long storyId);

    StorySummary add(Long promptId, String storyTitle);

    void validateInputForNewStory(StoryWithChapterInput storyInput);
}
