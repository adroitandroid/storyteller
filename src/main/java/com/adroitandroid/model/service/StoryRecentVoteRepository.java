package com.adroitandroid.model.service;

import com.adroitandroid.model.StoryRecentVote;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.math.BigInteger;

/**
 * Created by pv on 07/01/17.
 */
public interface StoryRecentVoteRepository extends MongoRepository<StoryRecentVote, BigInteger>, RecentStoryVoteRepositoryCustom {
    StoryRecentVote findByUserIdAndSnippetId(Long userId, Long snippetId);
}
