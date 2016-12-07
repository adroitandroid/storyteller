package com.adroitandroid.model.service;

import com.adroitandroid.model.*;
import com.adroitandroid.network.ServiceGenerator;
import com.adroitandroid.network.entity.FcmPushBody;
import com.adroitandroid.network.entity.FcmResponse;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

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
    private final ChapterStatsRepository chapterStatsRepository;
    private final UserDetailRepository userDetailRepository;

    public ChapterServiceImpl(ChapterRepository chapterRepository,
                              StorySummaryRepository storySummaryRepository,
                              NotificationRepository notificationRepository,
                              ChapterDetailRepository chapterDetailRepository,
                              GenreRepository genreRepository,
                              ChapterGenreRepository chapterGenreRepository,
                              StoryGenreRepository storyGenreRepository,
                              ChapterStatsRepository chapterStatsRepository,
                              UserDetailRepository userDetailRepository) {
        this.chapterRepository = chapterRepository;
        this.storySummaryRepository = storySummaryRepository;
        this.notificationRepository = notificationRepository;
        this.chapterDetailRepository = chapterDetailRepository;
        this.genreRepository = genreRepository;
        this.chapterGenreRepository = chapterGenreRepository;
        this.storyGenreRepository = storyGenreRepository;
        this.chapterStatsRepository = chapterStatsRepository;
        this.userDetailRepository = userDetailRepository;
    }

    @Override
    public void validateAddChapterInput(ChapterInput input, boolean prevChapterRequired) {
        if (input.userId == null) {
            throw new IllegalArgumentException("User id unspecified");
        }
        if (input.storyId == null || (input.previousChapterId == null && prevChapterRequired)) {
            throw new IllegalArgumentException("Story information missing");
        }
        if (isEmpty(input.chapterPlot) || isEmpty(input.chapterTitle)) {
            throw new IllegalArgumentException("Chapter information missing");
        }
        if (prevChapterRequired) {
            Chapter previousChapter = chapterRepository.findOne(input.previousChapterId);
            if (previousChapter.getStatus() != Chapter.STATUS_PUBLISHED
                    || (previousChapter.endsStory != null && previousChapter.endsStory)) {
                throw new IllegalArgumentException("Illegal state for a previous chapter");
            }
        }
    }

    @Override
    public Chapter addChapter(ChapterInput chapterInput) {
        StorySummary storySummary;
        Chapter prevChapter = null;
        List<Long> traversedChapterIds = new ArrayList<>();
        if (chapterInput.previousChapterId != null) {
            prevChapter = chapterRepository.findOne(chapterInput.previousChapterId);
            storySummary = prevChapter.getStorySummary();
            List<Long> prevChapterTraversal = prevChapter.getTraversal();
            if (isEmpty(prevChapterTraversal)) {
                traversedChapterIds = new ArrayList<>();
            } else {
                traversedChapterIds = prevChapterTraversal;
            }
            traversedChapterIds.add(prevChapter.getId());
        } else {
            storySummary = storySummaryRepository.findOne(chapterInput.storyId);
        }
        Chapter newChapter = new Chapter(chapterInput.chapterTitle, chapterInput.chapterPlot,
                chapterInput.previousChapterId, chapterInput.userId, storySummary, traversedChapterIds,
                chapterInput.previousChapterId == null ? Chapter.STATUS_AUTO_APPROVED : Chapter.STATUS_UNAPPROVED);
        Chapter chapter = chapterRepository.save(newChapter);
        if (prevChapter != null) {
            Notification newNotification = new Notification(prevChapter, chapter, Notification.TYPE_APPROVAL_REQUEST);
            notificationRepository.save(newNotification);

            sendFcmPush(newNotification, true, false);
        }
        return chapter;
    }

    @Override
    public void addChapterApproval(Long chapterId, Long notificationId, Boolean approval) {
        Timestamp currentTime = getCurrentTime();
        chapterRepository.updateStatus(
                chapterId, approval ? Chapter.STATUS_APPROVED : Chapter.STATUS_REJECTED, currentTime);
        Notification notificationForApproval = notificationRepository.findOne(notificationId);
        updateNotificationStatus(notificationForApproval, Notification.STATUS_READ);
        Notification notification = new Notification(notificationForApproval.senderChapter,
                notificationForApproval.receiverChapter, Notification.TYPE_APPROVED_NOTIFICATION);
        notificationRepository.save(notification);

        sendFcmPush(notification, false, approval);
    }

    private void sendFcmPush(Notification notification, boolean isApprovalRequest, boolean approvalResponse) {
        Long userId = notification.receiverUser.getId();
        UserDetail userDetail = userDetailRepository.findByUserId(userId);
        if (userDetail != null && userDetail.fcmToken != null) {
            ServiceGenerator.getFcmService().sendPush(new FcmPushBody(userDetail.fcmToken, isApprovalRequest, approvalResponse))
                    .enqueue(new Callback<FcmResponse>() {
                        @Override
                        public void onResponse(Call<FcmResponse> call, Response<FcmResponse> response) {
                            if (response.isSuccessful()) {
                                List<FcmResponse.FcmResult> fcmResults = response.body().getResults();
                                FcmResponse.FcmResult fcmResult = fcmResults.get(0);
                                if (fcmResult.isSuccess()) {
                                    updateNotificationStatus(notification, Notification.STATUS_SENT);
                                    String newToken = fcmResult.getRegistrationId();
                                    if (newToken != null) {
                                        userDetailRepository.updateToken(userId, newToken, getCurrentTime());
                                    }
                                } else if (fcmResult.shouldRemove()) {
                                    updateNotificationStatus(notification, Notification.STATUS_UNSENT_BAD_DETAILS);
                                    userDetailRepository.updateToken(userId, null, getCurrentTime());
                                } else if (fcmResult.shouldResend()) {
                                    updateNotificationStatus(notification, Notification.STATUS_UNSENT_QUEUED);
                                } else if (fcmResult.isUnrecoverableError()) {
                                    updateNotificationStatus(notification, Notification.STATUS_UNSENT_FCM_UNRECOVERABLE_FAILURE);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<FcmResponse> call, Throwable throwable) {
                            updateNotificationStatus(notification, Notification.STATUS_UNSENT_FCM_FAILURE);
                        }
                    });
        } else {
            updateNotificationStatus(notification, Notification.STATUS_UNSENT_BAD_DETAILS);
        }
    }

    private void updateNotificationStatus(Notification notification, Integer newStatus) {
        notification.setNewStatus(newStatus);
        notification.updateUpdatedTime();
        notificationRepository.save(notification);
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
        Chapter newChapter = chapterRepository.findOne(chapterContent.getChapterId());
        if (newChapter.parentChapterId != null) {
//            If new author came after notified of proposal approval, mark it as read
            markNotificationReadForReceiverChapter(newChapter, Notification.TYPE_APPROVED_NOTIFICATION);
        }
        return savedChapterDetail;
    }

    public void markNotificationReadForReceiverChapter(Chapter receiverChapter, Integer notificationType) {
        List<Notification> notificationList
                = notificationRepository.findByReceiverChapterAndNotificationType(receiverChapter, notificationType);
        notificationList.forEach((notification) -> updateNotificationStatus(notification, Notification.STATUS_READ));
    }

    @Override
    public List<Chapter> findAllChaptersByAuthorIdWithStatus(Long userId, boolean includeSoftDeleted, Integer... status) {
        if (includeSoftDeleted) {
            if (status.length == 1) {
                return chapterRepository.findByAuthorUserIdAndStatusOrderByUpdatedAtDesc(userId, status[0]);
            } else {
                return chapterRepository.findByAuthorUserIdAndStatusInOrderByUpdatedAtDesc(userId, Arrays.asList(status));
            }
        } else {
            if (status.length == 1) {
                return chapterRepository.findByAuthorUserIdAndStatusAndSoftDeletedFalseOrderByUpdatedAtDesc(userId, status[0]);
            } else {
                return chapterRepository.findByAuthorUserIdAndStatusInAndSoftDeletedFalseOrderByUpdatedAtDesc(userId, Arrays.asList(status));
            }
        }
    }

    @Override
    public void insertChapterStats(Long chapterId) {
        chapterStatsRepository.save(chapterId);
    }

    @Override
    public void incrementReadsFor(Chapter chapter) {
        chapterStatsRepository.incrementReads(chapter.getId());
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
    public Chapter updateSummaryAndGenresForChapterAndStory(Chapter chapter, boolean endsStory,
                                                            int statusPublished, List<String> genreNames) {
        List<Genre> genres = genreRepository.findByNameIn(genreNames);
        List<ChapterGenre> chapterGenres = genres.stream().map(genre -> new ChapterGenre(chapter, genre)).collect(Collectors.toList());
        chapterGenreRepository.save(chapterGenres);

        if (endsStory) {
            chapterRepository.updateStatusAndEndFlag(chapter.getId(), statusPublished, true, getCurrentTime());
            return updateStoryGenresCount(chapter.getStorySummary(), chapter.getId());
        } else {
            chapterRepository.updateStatus(chapter.getId(), statusPublished, getCurrentTime());
            return null;
        }
    }

    private Chapter updateStoryGenresCount(StorySummary storySummary, Long currentChapterId) {
        Chapter chapterToReturn = null;
        List<Chapter> endingChapters
                = chapterRepository.findByStorySummaryAndEndsStoryTrueAndStatusAndSoftDeletedFalse(
                storySummary, Chapter.STATUS_PUBLISHED);
        Set<Long> chaptersInCompletedStory = new HashSet<>();
        for (Chapter chapter : endingChapters) {
            if (!isEmpty(chapter.getTraversal())) {
                chaptersInCompletedStory.addAll(chapter.getTraversal());
            }
            chaptersInCompletedStory.add(chapter.getId());

            if (chapter.getId().equals(currentChapterId)) {
                chapterToReturn = chapter;
            }
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
        return chapterToReturn;
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
