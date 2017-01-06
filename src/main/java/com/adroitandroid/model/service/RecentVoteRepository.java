package com.adroitandroid.model.service;

import com.adroitandroid.model.RecentVote;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.math.BigInteger;

/**
 * Created by pv on 06/01/17.
 */
public interface RecentVoteRepository extends MongoRepository<RecentVote, BigInteger>, RecentVoteRepositoryCustom {
    RecentVote findByUserIdAndSnippetId(Long userId, Long snippetId);
}
