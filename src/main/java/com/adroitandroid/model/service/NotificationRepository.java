package com.adroitandroid.model.service;

import com.adroitandroid.model.Chapter;
import com.adroitandroid.model.Notification;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by pv on 30/11/16.
 */
interface NotificationRepository extends CrudRepository<Notification, Long> {

    List<Notification> findByReceiverChapterAndNotificationType(Chapter receiverChapter, Integer notificationType);

    List<Notification> findByReceiverUserIdAndStatusIsBetweenOrderByCreatedAtAsc(Long userId,
                                                                                 Integer statusCreated,
                                                                                 Integer statusRead);

    List<Notification> findByReceiverUserIdAndStatusOrderByCreatedAtDesc(Long userId, Integer statusDelivered);

    long countByReceiverUserIdAndStatusIsBetween(Long userId, Integer statusCreated, Integer statusDelivered);
}
