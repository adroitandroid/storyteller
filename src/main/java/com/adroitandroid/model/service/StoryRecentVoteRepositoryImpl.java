package com.adroitandroid.model.service;

import com.adroitandroid.model.RecentSnippet;
import com.adroitandroid.model.SnippetRecentVote;
import com.adroitandroid.model.StoryRecentVote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;

import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;

/**
 * Created by pv on 06/01/17.
 */
public class StoryRecentVoteRepositoryImpl implements RecentStoryVoteRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public StoryRecentVoteRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<RecentSnippet> getRecentStories() {
        return mongoTemplate.aggregate(
                Aggregation.newAggregation(group("snippetId")
                        .last("snippetId").as("snippetId")
                        .count().as("frequency")
                ), StoryRecentVote.class, RecentSnippet.class).getMappedResults();
    }
}
