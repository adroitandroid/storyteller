package com.adroitandroid.model.service;

import com.adroitandroid.model.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by pv on 30/11/16.
 */
@Component("chapterService")
@Transactional
public class ChapterServiceImpl implements ChapterService {
    private final ChapterRepository chapterRepository;
    private final StorySummaryRepository storySummaryRepository;
    private final NotificationRepository notificationRepository;
    private final ChapterDetailRepository chapterDetailRepository;

    public ChapterServiceImpl(ChapterRepository chapterRepository,
                              StorySummaryRepository storySummaryRepository,
                              NotificationRepository notificationRepository,
                              ChapterDetailRepository chapterDetailRepository) {
        this.chapterRepository = chapterRepository;
        this.storySummaryRepository = storySummaryRepository;
        this.notificationRepository = notificationRepository;
        this.chapterDetailRepository = chapterDetailRepository;
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
        Chapter prevChapter = chapterRepository.findOne(chapterInput.previousChapterId);
        StorySummary storySummary;
        String traversal;
        if (prevChapter != null) {
            storySummary = prevChapter.getStorySummary();
            String prevChapterTraversal = prevChapter.getTraversal();
            List<Long> traversedChapterIds;
            if (prevChapterTraversal == null || prevChapterTraversal.isEmpty()) {
                traversedChapterIds = new ArrayList<>();
            } else {
                Type collectionType = new TypeToken<List<Long>>() {
                }.getType();
                Gson gson = new Gson();
                traversedChapterIds = gson.fromJson(prevChapterTraversal, collectionType);
            }
            traversedChapterIds.add(prevChapter.getId());
            traversal = new Gson().toJson(traversedChapterIds);
        } else {
            storySummary = storySummaryRepository.findOne(chapterInput.storyId);
            traversal = "";
        }
        Chapter newChapter = new Chapter(chapterInput.chapterTitle, chapterInput.chapterPlot,
                chapterInput.previousChapterId, chapterInput.userId, storySummary, traversal);
        Chapter chapter = chapterRepository.save(newChapter);
        Notification newNotification = new Notification(prevChapter, chapter, Notification.TYPE_APPROVAL_REQUEST);
        notificationRepository.save(newNotification);
//        TODO: send notification as well, TBD
        return chapter;
    }

    @Override
    public void addChapterApproval(Long chapterId, Long notificationId, Boolean approval) {
        Timestamp currentTime = getCurrentTime();
        chapterRepository.updateStatus(
                chapterId, approval ? Chapter.STATUS_APPROVED : Chapter.STATUS_REJECTED, currentTime);
        notificationRepository.updateReadStatus(notificationId, currentTime, true);
    }

    @Override
    public void editChapter(ChapterDetail chapterDetail) {
        chapterDetailRepository.update(chapterDetail.getId(), chapterDetail.getContent(), getCurrentTime());
    }

    @Override
    public ChapterDetail addContent(ChapterContent chapterContent) {
        ChapterDetail chapterDetail = new ChapterDetail(chapterContent.getContent());
        ChapterDetail savedChapterDetail = chapterDetailRepository.save(chapterDetail);
        chapterRepository.putChapterDetailId(chapterContent.getChapterId(), savedChapterDetail.getId(), getCurrentTime());
        return savedChapterDetail;
    }

    private Timestamp getCurrentTime() {
        return new Timestamp((new Date()).getTime());
    }

    private boolean isEmpty(String input) {
        return input == null || input.isEmpty();
    }
}
