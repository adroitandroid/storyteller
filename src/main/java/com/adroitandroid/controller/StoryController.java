package com.adroitandroid.controller;

import com.adroitandroid.model.Chapter;
import com.adroitandroid.model.StoryGenres;
import com.adroitandroid.model.StorySummary;
import com.adroitandroid.model.service.StoryService;
import com.google.gson.JsonElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by pv on 30/11/16.
 */
@RestController
@RequestMapping(value = "/story")
public class StoryController extends AbstractController {
    @Autowired
    private StoryService storyService;

    @RequestMapping(value = "/", method = RequestMethod.GET, produces = "application/json")
    public JsonElement getAllCompletedStories() {
        List<StorySummary> allCompletedStories = storyService.getAllCompletedStories();
        return prepareResponseFrom(allCompletedStories,
                StorySummary.PROMPT, StorySummary.STORY_STATS, StorySummary.STORY_GENRES, StoryGenres.GENRE);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "application/json")
    public JsonElement getEntireStoryById(@PathVariable long id) {
        StorySummary storySummary = storyService.getCompleteStoryById(id);
        return prepareResponseFrom(storySummary, StorySummary.CHAPTERS, Chapter.CHAPTER_DETAIL);
    }
}
