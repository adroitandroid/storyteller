package com.adroitandroid.model.service;

import com.adroitandroid.model.UserDetails;
import com.adroitandroid.model.UserLoginInfo;
import com.adroitandroid.model.UserSession;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.CompletableFuture;

/**
 * Created by pv on 29/10/16.
 */
public interface UserService {

    int SESSION_EXPIRY_IN_SEC = 7200;
    int REGEN_WINDOW_IN_SEC = 300;

    CompletableFuture<UserDetails> signIn(UserLoginInfo userLoginInfo) throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException;

    UserSession getUserSessionForAuthToken(String sessionId) throws IOException;
}
