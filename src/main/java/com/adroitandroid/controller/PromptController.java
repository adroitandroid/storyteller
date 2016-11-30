package com.adroitandroid.controller;

import com.adroitandroid.model.Chapter;
import com.adroitandroid.model.Prompt;
import com.adroitandroid.model.StorySummary;
import com.adroitandroid.model.service.PromptService;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by pv on 29/11/16.
 */
@RestController
@RequestMapping(value = "/prompt")
public class PromptController extends AbstractController {

    @Autowired
    private PromptService promptService;

    @RequestMapping(value = "/", method = RequestMethod.GET, produces = "application/json")
    public JsonElement getAllActivePrompts() {
        List<Prompt> promptList = promptService.getAllActivePromptsSortedByUpdateTime();
        return prepareResponseFrom(promptList);
    }

    @RequestMapping(value = "/{promptId}", method = RequestMethod.GET, produces = "application/json")
    public Prompt getAllStoryChaptersForPrompt(@PathVariable long promptId) {
        Prompt prompt = promptService.getAllStoryChaptersForPromptId(promptId);
        JsonElement jsonElement = prepareResponseFrom(prompt, Prompt.STORIES, StorySummary.CHAPTERS);
        Prompt promptWithFetches = new Gson().fromJson(jsonElement, Prompt.class);

//        Removing all unapproved chapters before returning
        List<StorySummary> storySummariesToRemove = new ArrayList<>();
        for (StorySummary storySummary : promptWithFetches.getStories()) {
            List<Chapter> chaptersToRemove = storySummary.getChapters().stream().filter(chapter -> chapter.getStatus() <= 0).collect(Collectors.toList());
            storySummary.getChapters().removeAll(chaptersToRemove);
            if (storySummary.getChapters().size() == 0) {
                storySummariesToRemove.add(storySummary);
            }
        }
        promptWithFetches.getStories().removeAll(storySummariesToRemove);
        return promptWithFetches;
    }
}
