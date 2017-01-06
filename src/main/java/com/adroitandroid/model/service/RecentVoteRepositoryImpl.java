package com.adroitandroid.model.service;

import com.adroitandroid.model.RecentSnippet;
import com.adroitandroid.model.RecentVote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;

import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;

/**
 * Created by pv on 06/01/17.
 */
public class RecentVoteRepositoryImpl implements RecentVoteRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public RecentVoteRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<RecentSnippet> getRecentSnippets() {
        return mongoTemplate.aggregate(
                Aggregation.newAggregation(group("snippetId")
                .last("snippetId").as("snippetId")
                .count().as("frequency")
        ), RecentVote.class, RecentSnippet.class).getMappedResults();
    }
}
