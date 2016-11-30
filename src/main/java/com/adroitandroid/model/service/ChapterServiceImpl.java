package com.adroitandroid.model.service;

import com.adroitandroid.model.Chapter;
import com.adroitandroid.model.ChapterInput;
import com.adroitandroid.model.Notification;
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
    private final NotificationRepository notificationRepository;

    public ChapterServiceImpl(ChapterRepository chapterRepository,
                              StorySummaryRepository storySummaryRepository,
                              NotificationRepository notificationRepository) {
        this.chapterRepository = chapterRepository;
        this.storySummaryRepository = storySummaryRepository;
        this.notificationRepository = notificationRepository;
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
        Chapter chapter = chapterRepository.save(newChapter);
        Chapter prevChapter = chapterRepository.findOne(chapterInput.previousChapterId);
        Notification newNotification = new Notification(prevChapter, chapter, Notification.TYPE_APPROVAL_REQUEST);
        notificationRepository.save(newNotification);
//        TODO: send notification as well, TBD
        return chapter;
    }

    private boolean isEmpty(String input) {
        return input == null || input.isEmpty();
    }
}
