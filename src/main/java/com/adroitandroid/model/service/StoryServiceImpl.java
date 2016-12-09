package com.adroitandroid.model.service;

import com.adroitandroid.model.Prompt;
import com.adroitandroid.model.StorySummary;
import com.adroitandroid.model.StoryWithChapterInput;
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
    private final PromptRepository promptRepository;

    public StoryServiceImpl(StorySummaryRepository storySummaryRepository,
                            StoryStatsRepository storyStatsRepository,
                            PromptRepository promptRepository) {
        this.storySummaryRepository = storySummaryRepository;
        this.storyStatsRepository = storyStatsRepository;
        this.promptRepository = promptRepository;
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
        storyStatsRepository.incrementCompleted(storyId);
    }

    @Override
    public StorySummary add(Long promptId, String storyTitle) {
        Prompt prompt = promptRepository.findOne(promptId);
        StorySummary storySummary = new StorySummary(storyTitle, prompt);
        return storySummaryRepository.save(storySummary);
    }

    @Override
    public void validateInputForNewStory(StoryWithChapterInput storyInput) {
        if (isEmpty(storyInput.storyTitle) || storyInput.promptId == null) {
            throw new IllegalArgumentException("Story cannot be created due to missing title or prompt id");
        }
    }

    @Override
    public void insertStoryStats(Long storySummaryId) {
        storyStatsRepository.save(storySummaryId);
    }

    @Override
    public void incrementReadsFor(StorySummary storySummary) {
        storyStatsRepository.incrementReads(storySummary.getId());
    }

    private boolean isEmpty(String input) {
        return input == null || input.isEmpty();
    }
}
