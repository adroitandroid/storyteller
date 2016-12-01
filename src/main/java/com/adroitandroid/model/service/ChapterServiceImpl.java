package com.adroitandroid.model.service;

import com.adroitandroid.model.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.*;

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
    private final GenreRepository genreRepository;
    private final ChapterGenreRepository chapterGenreRepository;
    private final StoryGenreRepository storyGenreRepository;

    public ChapterServiceImpl(ChapterRepository chapterRepository,
                              StorySummaryRepository storySummaryRepository,
                              NotificationRepository notificationRepository,
                              ChapterDetailRepository chapterDetailRepository,
                              GenreRepository genreRepository,
                              ChapterGenreRepository chapterGenreRepository,
                              StoryGenreRepository storyGenreRepository) {
        this.chapterRepository = chapterRepository;
        this.storySummaryRepository = storySummaryRepository;
        this.notificationRepository = notificationRepository;
        this.chapterDetailRepository = chapterDetailRepository;
        this.genreRepository = genreRepository;
        this.chapterGenreRepository = chapterGenreRepository;
        this.storyGenreRepository = storyGenreRepository;
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
        Chapter previousChapter = chapterRepository.findOne(input.previousChapterId);
        if (previousChapter.getStatus() != Chapter.STATUS_PUBLISHED
                || (previousChapter.endsStory != null && previousChapter.endsStory)) {
            throw new IllegalArgumentException("Illegal state for a previous chapter");
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
            if (isEmpty(prevChapterTraversal)) {
                traversedChapterIds = new ArrayList<>();
            } else {
                traversedChapterIds = getChapterIdListFromTraversalString(prevChapterTraversal);
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

    private List<Long> getChapterIdListFromTraversalString(String prevChapterTraversal) {
        List<Long> traversedChapterIds;Type collectionType = new TypeToken<List<Long>>() {
        }.getType();
        Gson gson = new Gson();
        traversedChapterIds = gson.fromJson(prevChapterTraversal, collectionType);
        return traversedChapterIds;
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

    @Override
    public Chapter validatePublishChapterInput(ChapterContentToPublish contentToPublish) {
        if (isEmpty(contentToPublish.getContent())) {
            throw new IllegalArgumentException("Empty chapter cannot be published");
        }
        if (isEmpty(contentToPublish.getGenreNames())) {
            throw new IllegalArgumentException("Chapter cannot be published without genres");
        }
        Chapter chapter = chapterRepository.findOne(contentToPublish.getChapterId());
        if (chapter.getStatus() != Chapter.STATUS_APPROVED && chapter.getStatus() != Chapter.STATUS_AUTO_APPROVED) {
            throw new IllegalArgumentException("Unapproved chapter cannot be published");
        }
        return chapter;
    }

    @Override
    public void updateSummaryAndChapterGenres(Chapter chapter, boolean endsStory, int statusPublished, List<String> genreNames) {
        List<Genre> genres = genreRepository.findByNameIn(genreNames);
        List<ChapterGenre> chapterGenres = new ArrayList<>();
        for (Genre genre : genres) {
            chapterGenres.add(new ChapterGenre(chapter, genre));
        }
        chapterGenreRepository.save(chapterGenres);

        if (endsStory) {
            chapterRepository.updateStatusAndEndFlag(chapter.getId(), statusPublished, true, getCurrentTime());
            updateStoryGenresCount(chapter.getStorySummary());
        } else {
            chapterRepository.updateStatus(chapter.getId(), statusPublished, getCurrentTime());
        }
    }

    private void updateStoryGenresCount(StorySummary storySummary) {
        List<Chapter> endingChapters
                = chapterRepository.findByStorySummaryAndEndsStoryTrueAndStatusAndSoftDeletedFalse(
                storySummary, Chapter.STATUS_PUBLISHED);
        Set<Long> chaptersInCompletedStory = new HashSet<>();
        for (Chapter chapter : endingChapters) {
            String endChapterTraversal = chapter.getTraversal();
            if (endChapterTraversal != null) {
                chaptersInCompletedStory.addAll(getChapterIdListFromTraversalString(endChapterTraversal));
            }
            chaptersInCompletedStory.add(chapter.getId());
        }
        List<ChapterGenre> chapterGenreList = chapterGenreRepository.findByChapterIdIn(chaptersInCompletedStory);
        Map<Genre, Integer> genreCount = new HashMap<>();
        for (ChapterGenre chapterGenre : chapterGenreList) {
            Genre genre = chapterGenre.getGenreId();
            Integer count = genreCount.get(genre);
            if (count == null) {
                genreCount.put(genre, 1);
            } else {
                genreCount.put(genre, count + 1);
            }
        }
        for (Genre genre : genreCount.keySet()) {
            storyGenreRepository.insertOnDuplicateKeyUpdate(storySummary.getId(), genre.getId(), genreCount.get(genre));
        }
    }

    private Timestamp getCurrentTime() {
        return new Timestamp((new Date()).getTime());
    }

    private boolean isEmpty(String input) {
        return input == null || input.isEmpty();
    }

    private boolean isEmpty(List list) {
        return list == null || list.size() == 0;
    }
}
