package com.adroitandroid.model.service;

import com.adroitandroid.model.StorySummary;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by pv on 30/11/16.
 */
@Component("storyService")
@Transactional
public class StoryServiceImpl implements StoryService {

    private final StorySummaryRepository storySummaryRepository;
    private final StoryStatsRepository storyStatsRepository;

    public StoryServiceImpl(StorySummaryRepository storySummaryRepository,
                            StoryStatsRepository storyStatsRepository) {
        this.storySummaryRepository = storySummaryRepository;
        this.storyStatsRepository = storyStatsRepository;
    }

    @Override
    public List<StorySummary> getAllCompletedStories() {
        return storySummaryRepository.findCompleted();
    }

    @Override
    public StorySummary getCompleteStoryById(long id) {
        return storySummaryRepository.findOne(id);
    }

    @Override
    public void incrementStoryCompletedCount(Long storyId) {
        storyStatsRepository.incrementCompletedCount(storyId);
    }
}
