package com.adroitandroid.model.service;

import com.adroitandroid.model.Notification;
import com.adroitandroid.model.User;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by pv on 01/12/16.
 */
@Component("notificationService")
@Transactional
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public Boolean anyUnreadNotificationForUserId(Long userId) {
        return notificationRepository.countByReceiverUserAndReadStatusFalse(new User(userId)) > 0;
    }

    @Override
    public List<Notification> getUnreadSortedByEdfAndReadSortedByLifoFor(Long userId) {
        List<Notification> notifications
                = notificationRepository.findByReceiverUserIdAndReadStatusFalseOrderByCreatedAtAsc(userId);
        notifications.addAll(notificationRepository.findByReceiverUserIdAndReadStatusTrueOrderByCreatedAtDesc(userId));
        return notifications;
    }
}
