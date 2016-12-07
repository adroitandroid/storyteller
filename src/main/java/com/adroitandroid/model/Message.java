package com.adroitandroid.model;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by pv on 06/12/16.
 */
public class Message {
    private final String messageFromUsername;
    private final String messageForStoryTitle;
    private final String messageForChapterTitle;
    private final Long millisBeforeExpiry;
    private final Response response;
    private final Chapter proposedChapterSummary;
    private final MessageType type;
    private final long id;

    public Message(Notification notification) {
        this.messageFromUsername = notification.senderUser.username;
        this.messageForChapterTitle = notification.receiverChapter.title;
        this.messageForStoryTitle = notification.receiverChapter.getStorySummary().getTitle();
        this.millisBeforeExpiry = getMillisBeforeExpiryFor(notification);
        this.response = getResponseFrom(notification);
        boolean approvedNotif = Notification.TYPE_APPROVED_NOTIFICATION.equals(notification.notificationType);
        this.proposedChapterSummary = approvedNotif ? notification.receiverChapter : notification.senderChapter;
        this.type = approvedNotif ? MessageType.getApprovalResponseType() : MessageType.getApprovalRequestType();
        this.id = notification.id;
    }

    private static Response getResponseFrom(Notification notification) {
        if (Notification.TYPE_APPROVED_NOTIFICATION.equals(notification.notificationType)) {
            return Response.forChapterStatus(notification.receiverChapter.getStatus());
        } else {
            return Response.forChapterStatus(notification.senderChapter.getStatus());
        }
    }

    private static Long getMillisBeforeExpiryFor(Notification notification) {
        Timestamp createdAt = notification.createdAt;
        Calendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(createdAt.getTime());
        calendar.add(Calendar.DAY_OF_MONTH, 2);
        long expiresAtTime = calendar.getTimeInMillis();
        long timeTillExpiry = expiresAtTime - System.currentTimeMillis();
        return timeTillExpiry > 0 ? timeTillExpiry : 0L;
    }

    private static class Response {
        static final int NONE = 0;
        static final int APPROVED = 1;
        static final int REJECTED = 2;
        static final int AUTO_APPROVED = 3;

        public static final long MIN_IN_MILLIS = 60000L;

        private int responseCode;

        Response(int code) {
            responseCode = code;
        }

        static Response forChapterStatus(int chapterStatus) {
            int code = NONE;
            switch (chapterStatus) {
                case Chapter.STATUS_APPROVED:
                case Chapter.STATUS_PUBLISHED:
                    code = APPROVED;
                    break;
                case Chapter.STATUS_AUTO_APPROVED:
                    code = AUTO_APPROVED;
                    break;
                case Chapter.STATUS_REJECTED:
                    code = REJECTED;
                    break;
                case Chapter.STATUS_UNAPPROVED:
                    code = NONE;
                    break;
            }
            return new Response(code);
        }
    }

    private static class MessageType {
        static final int APPROVAL_REQUEST = 1;
        static final int APPROVAL_RESPONSE = 2;

        private final int type;

        private MessageType(int type) {
            this.type = type;
        }

        public int getType() {
            return type;
        }

        static MessageType getApprovalRequestType() {
            return new MessageType(APPROVAL_REQUEST);
        }

        static MessageType getApprovalResponseType() {
            return new MessageType(APPROVAL_RESPONSE);
        }
    }
}
