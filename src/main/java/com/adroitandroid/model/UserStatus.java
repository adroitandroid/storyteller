package com.adroitandroid.model;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by pv on 08/01/17.
 */
@Entity
@Table(name = "user_status")
public class UserStatus {

    public static final String EVENT_ELIGIBLE_TO_ADD_SNIPPET = "eligibleToAddSnippet";
    public static final String EVENT_INITIAL_SNIPPETS_USED = "initialSnippets";
    public static final int INITIAL_SNIPPET_COUNT = 5;
    private static final int STATUS_TRUE = 1;
    private static final int STATUS_FALSE = 0;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "user_id")
    private Long userId;
    private String event;
    private Integer status;
    @Column(name = "updated_at")
    private Timestamp updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public static UserStatus getInitialSnippetsStatus(Long userId, Timestamp currentTime) {
        UserStatus userStatus = new UserStatus();
        userStatus.setUpdatedAt(currentTime);
        userStatus.setEvent(EVENT_INITIAL_SNIPPETS_USED);
        userStatus.setStatus(STATUS_FALSE);
        userStatus.setUserId(userId);
        return userStatus;
    }

    public static UserStatus getEligibleToAddSnippetsStatus(Long userId, Timestamp currentTime) {
        UserStatus userStatus = new UserStatus();
        userStatus.setUpdatedAt(currentTime);
        userStatus.setEvent(EVENT_ELIGIBLE_TO_ADD_SNIPPET);
        userStatus.setStatus(INITIAL_SNIPPET_COUNT);
        userStatus.setUserId(userId);
        return userStatus;
    }
}
