package com.adroitandroid.controller;

import com.adroitandroid.model.*;
import com.adroitandroid.model.service.ChapterService;
import com.adroitandroid.model.service.StoryService;
import com.adroitandroid.model.service.UserService;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    @Autowired
    private UserService userService;

    @RequestMapping(value = "/", method = RequestMethod.GET, produces = "application/json")
    public JsonElement getAllCompletedStories() {
        List<StorySummary> allCompletedStories = storyService.getAllCompletedStories();
        return prepareResponseFrom(allCompletedStories,
                StorySummary.PROMPT, StorySummary.STORY_STATS, StorySummary.STORY_GENRES, StoryGenre.GENRE);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "application/json")
    public JsonObject getEntireStoryById(@PathVariable long id, @RequestParam(value = "only_complete",
            required = false, defaultValue = "true") boolean onlyComplete) {
        returnIfUnauthenticatedUserSession();
        Long userId = getUserIdFromRequest();

        StorySummary storySummary = storyService.getCompleteStoryById(id);
        JsonElement jsonElement = prepareResponseFrom(storySummary,
                StorySummary.PROMPT, StorySummary.CHAPTERS, Chapter.CHAPTER_DETAIL);
        StorySummary storySummaryWithChapterContent = new Gson().fromJson(jsonElement, StorySummary.class);

        List<Chapter> chaptersToRemove = storySummaryWithChapterContent.getChapters().stream().filter(chapter
                -> !isPublishedOrUserDraft(chapter, userId)).collect(Collectors.toList());
        if (onlyComplete) {
            List<Chapter> chaptersToRetain = new ArrayList<>();
            Set<Long> chapterIdsToRetain = new HashSet<>();
            storySummaryWithChapterContent.getChapters().stream().filter(chapter
                    -> chapter.endsStory != null && chapter.endsStory).forEach(chapter -> {
                chaptersToRetain.add(chapter);
                List<Long> traversal = chapter.getTraversal();
                if (traversal != null) {
                    chapterIdsToRetain.addAll(traversal);
                }
            });
            chaptersToRetain.addAll(storySummaryWithChapterContent.getChapters().stream().filter(chapter
                    -> chapterIdsToRetain.contains(chapter.getId())).collect(Collectors.toList()));

            storySummaryWithChapterContent.getChapters().retainAll(chaptersToRetain);
        }
        if (chaptersToRemove != null) {
            storySummaryWithChapterContent.getChapters().removeAll(chaptersToRemove);
        }

        JsonElement jsonTree = new Gson().toJsonTree(storySummaryWithChapterContent);
        JsonObject jsonObject = jsonTree.getAsJsonObject();
        if (userId != null && userId > 0) {
            List<UserChapterRelation> userBookmarksForChapters
                    = userService.getUserBookmarksFromChapters(userId, storySummaryWithChapterContent.getChapters());

            boolean hasUserLikedStory = userService.hasUserLikedStory(userId, storySummaryWithChapterContent.getId());
            jsonObject.addProperty("isLiked", hasUserLikedStory);

            List<Long> bookmarkedChapterIdList = new ArrayList<>();
            for (UserChapterRelation userChapterRelation : userBookmarksForChapters) {
                bookmarkedChapterIdList.add(userChapterRelation.getChapter().getId());
            }
            jsonObject.add("bookmarkedChapters", new Gson().toJsonTree(bookmarkedChapterIdList));

        }
        return jsonObject;
    }

    private boolean isPublishedOrUserDraft(Chapter chapter, Long userId) {
        Integer chapterStatus = chapter.getStatus();
        return chapterStatus == Chapter.STATUS_PUBLISHED
                || ((chapterStatus == Chapter.STATUS_AUTO_APPROVED
                || chapterStatus == Chapter.STATUS_APPROVED)
                && chapter.getAuthorUserId().equals(userId));
    }

    /**
     * Called when new story is added, irrespective of whether saved as draft or published
     * @param storyInput
     * @return
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = "application/json")
    public JsonElement addNewStory(@RequestBody StoryWithChapterInput storyInput) {
        storyInput.userId = needUserId();
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

    @RequestMapping(value = "/read", method = RequestMethod.PUT)
    public void chapterRead(@RequestBody StorySummary storySummary) {
        storyService.incrementReadsFor(storySummary);
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
