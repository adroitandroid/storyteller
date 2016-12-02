package com.adroitandroid.model.service;

import com.adroitandroid.model.UserSession;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by pv on 02/12/16.
 */
public interface UserSessionRepository extends MongoRepository<UserSession, String> {
    UserSession findByAuthTypeAndAuthUserIdAndAccessToken(String authenticationType, String userId, String accessToken);

    UserSession findBySessionId(String sessionId);
}
