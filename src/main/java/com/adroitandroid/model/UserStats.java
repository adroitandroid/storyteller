package com.adroitandroid.model;

import com.adroitandroid.serializer.OptionalInGson;
import org.hibernate.annotations.*;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Timestamp;

/**
 * Created by pv on 08/01/17.
 */
@Entity
@Table(name = "user_stats")
public class UserStats {
    public static final String USER_IN_USER_STATS = "user_in_user_stats";

    @Id
    @Column(name = "user_id")
    @GeneratedValue(generator = "gen")
    @GenericGenerator(name = "gen", strategy = "foreign", parameters = @Parameter(name = "property", value = "user"))
    private Long userId;

    @OptionalInGson(exclude = USER_IN_USER_STATS)
    @OneToOne
    @PrimaryKeyJoinColumn
    public User user;

    @Column(name = "net_votes")
    private Long netVotes;

    @Column(name = "num_followers")
    private Long numFollowers;

    @Column(name = "num_snippets")
    private Long numSnippets;

    public UserStats() {
//        empty constructor required by hibernate
    }


    public UserStats(User user) {
        this.user = user;
        this.numFollowers = 0L;
        this.netVotes = 0L;
        this.numSnippets = 0L;
    }

    public Long getNumSnippets() {
        return numSnippets;
    }

    public void setNumSnippets(long numSnippets) {
        this.numSnippets = numSnippets;
    }
}
