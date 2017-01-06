package com.adroitandroid.model;

import com.adroitandroid.serializer.OptionalInGson;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by pv on 06/01/17.
 */
@Entity
@Table(name = "user_votes")
public class UserSnippetVote implements Serializable {
    public static final String USER_IN_USER_VOTES = "user_in_uservotes";
    public static final String SNIPPET_IN_USER_VOTES = "snippet_in_user_votes";

    private Long id;

    @OptionalInGson(exclude = USER_IN_USER_VOTES)
    private User user;

    @OptionalInGson(exclude = SNIPPET_IN_USER_VOTES)
    private Snippet snippet;

    private Integer vote;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getVote() {
        return vote;
    }

    public void setVote(Integer vote) {
        this.vote = vote;
    }

    @ManyToOne
    @JoinColumn(name = "user_id")
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @ManyToOne
    @JoinColumn(name = "snippet_id")
    public Snippet getSnippet() {
        return snippet;
    }

    public void setSnippet(Snippet snippet) {
        this.snippet = snippet;
    }

    public void update() {
        this.updatedAt = new Timestamp((new Date()).getTime());
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
}
