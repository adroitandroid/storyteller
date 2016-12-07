package com.adroitandroid.model;

import com.adroitandroid.serializer.OptionalInGson;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by pv on 30/11/16.
 */
@Entity
@Table(name = "notifications")
public class Notification {

    public static final Integer TYPE_APPROVAL_REQUEST = 1;
    public static final Integer TYPE_APPROVED_NOTIFICATION = 2;

    public static final Integer STATUS_CREATED = 0;
    public static final Integer STATUS_SENT = 1;
    public static final Integer STATUS_DELIVERED = 2;
    public static final Integer STATUS_READ = 3;
    public static final Integer STATUS_UNSENT_QUEUED = -1;
    public static final Integer STATUS_UNSENT_FCM_FAILURE = -2;
    public static final Integer STATUS_UNSENT_FCM_UNRECOVERABLE_FAILURE = -3;
    public static final Integer STATUS_UNSENT_BAD_DETAILS = -4;

    public static final String RECEIVER_CHAPTER = "receiver_chapter_in_notifications";
    public static final String SENDER_CHAPTER = "sender_chapter_in_notifications";
    public static final String RECEIVER_USER = "receiver_user_in_notifications";
    public static final String SENDER_USER = "sender_user_in_notifications";

    public Notification(Chapter receiverChapter, Chapter senderChapter, Integer type) {
        this.notificationType = type;
        this.status = STATUS_CREATED;
        this.receiverChapter = receiverChapter;
        this.senderChapter = senderChapter;
        this.receiverUser = new User(receiverChapter.getAuthorUserId());
        this.senderUser = new User(senderChapter.getAuthorUserId());
        updateCreatedAndUpdatedTime();
    }

    public Notification() {
//        Empty as required by hibernate
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    @OptionalInGson(exclude = RECEIVER_USER)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_user_id")
    public User receiverUser;

    @OptionalInGson(exclude = RECEIVER_CHAPTER)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_chapter_id")
    public Chapter receiverChapter;

    @OptionalInGson(exclude = SENDER_USER)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_user_id")
    public User senderUser;

    @OptionalInGson(exclude = SENDER_CHAPTER)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_chapter_id")
    public Chapter senderChapter;

    @Column(name = "notification_type")
    public Integer notificationType;

    @Column(name = "status")
    private Integer status;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Column(name = "created_at")
    public Timestamp createdAt;

    private void updateCreatedAndUpdatedTime() {
        this.createdAt = new Timestamp((new Date()).getTime());
        this.updatedAt = this.createdAt;
    }

    public void updateUpdatedTime() {
        this.updatedAt = new Timestamp((new Date()).getTime());
    }

    public void setNewStatus(Integer newStatus) {
        this.status = newStatus;
    }
}
