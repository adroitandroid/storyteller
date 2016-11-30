package com.adroitandroid.model.service;

import com.adroitandroid.model.Chapter;
import com.adroitandroid.model.ChapterInput;
import com.adroitandroid.model.StorySummary;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by pv on 30/11/16.
 */
@Component("chapterService")
@Transactional
public class ChapterServiceImpl implements ChapterService {
    private final ChapterRepository chapterRepository;
    private final StorySummaryRepository storySummaryRepository;

    public ChapterServiceImpl(ChapterRepository chapterRepository, StorySummaryRepository storySummaryRepository) {
        this.chapterRepository = chapterRepository;
        this.storySummaryRepository = storySummaryRepository;
    }

    @Override
    public void validateAddChapterInput(ChapterInput input) {
        if (input.userId == null) {
            throw new IllegalArgumentException("User id unspecified");
        }
        if (input.storyId == null || input.previousChapterId == null) {
            throw new IllegalArgumentException("Story information missing");
        }
        if (isEmpty(input.chapterPlot) || isEmpty(input.chapterTitle)) {
            throw new IllegalArgumentException("Chapter information missing");
        }
    }

    @Override
    public Chapter addChapter(ChapterInput chapterInput) {
        StorySummary storySummary = storySummaryRepository.findOne(chapterInput.storyId);
        Chapter newChapter = new Chapter(chapterInput.chapterTitle, chapterInput.chapterPlot,
                chapterInput.previousChapterId, chapterInput.userId, storySummary);
        return chapterRepository.save(newChapter);
    }

    private boolean isEmpty(String input) {
        return input == null || input.isEmpty();
    }
}
