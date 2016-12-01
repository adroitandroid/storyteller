package com.adroitandroid.model;

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

    public Notification(Chapter receiverChapter, Chapter senderChapter, Integer type) {
        this.notificationType = type;
        this.readStatus = false;
        this.receiverChapter = receiverChapter;
        this.senderChapter = senderChapter;
        this.receiverUserId = receiverChapter.getAuthorUserId();
        this.senderUserId = senderChapter.getAuthorUserId();
        updateCreatedAndUpdatedTime();
    }

    public Notification() {
//        Empty as required by hibernate
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "receiver_user_id")
    private Long receiverUserId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_chapter_id")
    public Chapter receiverChapter;

    @Column(name = "sender_user_id")
    private Long senderUserId;

    @OneToOne(fetch = FetchType.LAZY)
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
