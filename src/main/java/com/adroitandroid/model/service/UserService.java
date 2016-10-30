package com.adroitandroid.model.service;

import com.adroitandroid.model.UserDetails;
import com.adroitandroid.model.UserLoginInfo;
import com.adroitandroid.model.UserSession;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.CompletableFuture;

/**
 * Created by pv on 29/10/16.
 */
public interface UserService {

    Long SESSION_EXPIRY_IN_MS = 7200000L;

    CompletableFuture<UserDetails> signIn(UserLoginInfo userLoginInfo) throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException;

    UserSession getUserSessionForSessionId(String sessionId);
}
