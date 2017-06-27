package com.adroitandroid.model.service;

import com.adroitandroid.model.SnippetRecentVote;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.math.BigInteger;

/**
 * Created by pv on 06/01/17.
 */
public interface SnippetRecentVoteRepository
        extends MongoRepository<SnippetRecentVote, BigInteger>, RecentSnippetVoteRepositoryCustom {
    SnippetRecentVote findByUserIdAndSnippetId(Long userId, Long snippetId);
}
