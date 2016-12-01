package com.adroitandroid.controller;

import com.adroitandroid.model.*;
import com.adroitandroid.model.service.ChapterService;
import com.adroitandroid.model.service.StoryService;
import com.google.gson.JsonElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by pv on 30/11/16.
 */
@RestController
@RequestMapping(value = "/story")
public class StoryController extends ChapterCreateUpdateController {
    @Autowired
    private StoryService storyService;
    @Autowired
    private ChapterService chapterService;

    @RequestMapping(value = "/", method = RequestMethod.GET, produces = "application/json")
    public JsonElement getAllCompletedStories() {
        List<StorySummary> allCompletedStories = storyService.getAllCompletedStories();
        return prepareResponseFrom(allCompletedStories,
                StorySummary.PROMPT, StorySummary.STORY_STATS, StorySummary.STORY_GENRES, StoryGenre.GENRE);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "application/json")
    public JsonElement getEntireStoryById(@PathVariable long id) {
        StorySummary storySummary = storyService.getCompleteStoryById(id);
        return prepareResponseFrom(storySummary, StorySummary.CHAPTERS, Chapter.CHAPTER_DETAIL);
    }

    /**
     * Called when new story is added, irrespective of whether saved as draft or published
     * @param storyInput
     * @return
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = "application/json")
    public JsonElement addNewStory(@RequestBody StoryWithChapterInput storyInput) {
        validateInputForNewStory(storyInput);
        StorySummary storySummary = addNewStorySummary(storyInput);
        getStoryService().insertStoryStats(storySummary.getId());
        Chapter chapter = validateAndAddNewChapter(new ChapterInput(storySummary.getId(), null,
                storyInput.chapterTitle, storyInput.chapterPlot, storyInput.userId), false);
        if (storyInput.genreNames == null) { //genre names are the only mandatory thing extra required for publishing
            addChapterContent(getChapterContentFrom(storyInput, chapter));
        } else {
            publishChapter(getChapterContentToPublishFrom(storyInput, chapter));
        }
        return prepareResponseFrom(storySummary, StorySummary.CHAPTERS, StorySummary.PROMPT);
    }

    private void validateInputForNewStory(StoryWithChapterInput storyInput) {
        storyService.validateInputForNewStory(storyInput);
    }

    private ChapterContentToPublish getChapterContentToPublishFrom(StoryWithChapterInput storyInput, Chapter chapter) {
        return new ChapterContentToPublish(chapter.getId(), storyInput.chapterContent, storyInput.endsStory,
                storyInput.genreNames);
    }

    private ChapterContent getChapterContentFrom(StoryWithChapterInput storyInput, Chapter chapter) {
        return new ChapterContent(chapter.getId(), storyInput.chapterContent);
    }

    private StorySummary addNewStorySummary(StoryWithChapterInput storyInput) {
        return storyService.add(storyInput.promptId, storyInput.storyTitle);
    }

    @Override
    StoryService getStoryService() {
        return storyService;
    }

    @Override
    ChapterService getChapterService() {
        return chapterService;
    }
}
