package com.adroitandroid.model.service;

import com.adroitandroid.model.Chapter;
import com.adroitandroid.model.Notification;
import com.adroitandroid.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by pv on 30/11/16.
 */
interface NotificationRepository extends CrudRepository<Notification, Long> {

    Notification findByReceiverChapter(Chapter receiverChapter);

    List<Notification> findByReceiverUserIdAndReadStatusFalseOrderByCreatedAtAsc(Long userId);

    List<Notification> findByReceiverUserIdAndReadStatusTrueOrderByCreatedAtDesc(Long userId);

    long countByReceiverUserAndReadStatusFalse(User user);
}
