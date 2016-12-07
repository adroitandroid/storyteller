package com.adroitandroid.model.service;

import com.adroitandroid.model.Notification;
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
        return notificationRepository.countByReceiverUserIdAndStatusIsBetween(userId,
                Notification.STATUS_CREATED, Notification.STATUS_DELIVERED) > 0;
    }

    @Override
    public List<Notification> getUnreadSortedByEdfAndReadSortedByLifoFor(Long userId) {
        List<Notification> notifications
                = notificationRepository.findByReceiverUserIdAndStatusIsBetweenOrderByCreatedAtAsc(
                        userId, Notification.STATUS_CREATED, Notification.STATUS_DELIVERED);
        notifications.addAll(notificationRepository.findByReceiverUserIdAndStatusOrderByCreatedAtDesc(userId, Notification.STATUS_READ));
        return notifications;
    }
}
