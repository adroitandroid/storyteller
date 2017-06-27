package com.adroitandroid.model;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Id;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

/**
 * Created by pv on 06/01/17.
 */
@CompoundIndex(name = "userSnippetVote", def = "{'snippetId' : 1, 'userId': 1}", unique = true)
@Document(collection = "storyRecentVotes")
public class StoryRecentVote implements Serializable {
    public static final int SECONDS_IN_A_DAY = 86400;

    @Id
    private BigInteger id;
    private Long snippetId;
    private Long userId;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Indexed(expireAfterSeconds = SECONDS_IN_A_DAY)
    private Date updateDate;

    public StoryRecentVote(Long snippetId, Long userId) {
        this.snippetId = snippetId;
        this.userId = userId;
        this.updateDate = new Date();
    }
}
