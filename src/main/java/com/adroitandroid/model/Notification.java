package com.adroitandroid.model;

import com.adroitandroid.OptionalInGson;

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

    public static final String RECEIVER_CHAPTER = "receiver_chapter_in_notifications";
    public static final String SENDER_CHAPTER = "sender_chapter_in_notifications";
    public static final String RECEIVER_USER = "receiver_user_in_notifications";
    public static final String SENDER_USER = "sender_user_in_notifications";

    public Notification(Chapter receiverChapter, Chapter senderChapter, Integer type) {
        this.notificationType = type;
        this.readStatus = false;
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
    private Long id;

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
    private Integer notificationType;

    @Column(name = "read_status")
    private Boolean readStatus;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Column(name = "created_at")
    private Timestamp createdAt;

    private void updateCreatedAndUpdatedTime() {
        this.createdAt = new Timestamp((new Date()).getTime());
        this.updatedAt = this.createdAt;
    }

    public void updateUpdatedTime() {
        this.updatedAt = new Timestamp((new Date()).getTime());
    }

    public void setReadStatusTrue() {
        this.readStatus = true;
    }
}
