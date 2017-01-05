package com.adroitandroid.model;

import com.adroitandroid.serializer.OptionalInGson;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by pv on 05/01/17.
 */
@Entity
@Table(name = "snippet_stats")
public class SnippetStats implements Serializable {
    public static final String SNIPPET = "snippet_in_snippet_stats";

    @Id
    @Column(name = "snippet_id")
    @GeneratedValue(generator = "gen")
    @GenericGenerator(name = "gen", strategy = "foreign", parameters = @Parameter(name = "property", value = "snippet"))
    private Long snippetId;

    @OptionalInGson(exclude = SNIPPET)
    @OneToOne
    @PrimaryKeyJoinColumn
    public Snippet snippet;

    @Column(name = "num_votes")
    private Long numVotes;

    @Column(name = "vote_sum")
    private Long voteSum;

    @Column(name = "num_children")
    private Long numChildren;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    public SnippetStats() {
//        empty constructor required by hibernate
    }


    public SnippetStats(Timestamp createdAt) {
        this.updatedAt = createdAt;
        this.numChildren = 0L;
        this.numVotes = 0L;
        this.voteSum = 0L;
    }
}
