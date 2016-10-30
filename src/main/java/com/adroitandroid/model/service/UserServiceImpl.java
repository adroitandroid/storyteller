package com.adroitandroid.model.service;

import com.adroitandroid.model.*;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Created by pv on 25/10/16.
 */
@Component("userService")
@Transactional
public class UserServiceImpl implements UserService {

    @Value("${facebook.uservalidation.url}")
    private String FACEBOOK_TOKEN_VALIDATION_URL;
    @Value("${facebook.appsecret}")
    private String FACEBOOK_APPSECRET;

    private static final RestTemplate restTemplate;

    static {
        restTemplate = new RestTemplate();
        SimpleClientHttpRequestFactory rf = (SimpleClientHttpRequestFactory) restTemplate.getRequestFactory();
        rf.setConnectTimeout(10 * 1000);
        rf.setReadTimeout(10 * 1000);
    }

    private final UserSessionRepository userSessionRepository;
    private final UserRepository userRepository;

    public UserServiceImpl(UserSessionRepository userSessionRepository, UserRepository userRepository) {
        this.userSessionRepository = userSessionRepository;
        this.userRepository = userRepository;
    }

    @Override
    public CompletableFuture<UserDetails> signIn(UserLoginInfo userLoginInfo)
            throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {
        Date currentTime = new Date();
        UserSession userSession = userSessionRepository.findByAuthTypeAndAuthUserIdAndAccessToken(
                userLoginInfo.getAuthenticationType(), userLoginInfo.getUserId(), userLoginInfo.getAccessToken());
        if (userSession != null && userSession.getExpiryTime().after(currentTime)) {
            return CompletableFuture.supplyAsync(() -> getUserDetailsForExistingUser(userSession, currentTime.getTime()));
        } else {
//            Delete existing session from cache since it's expired
            if (userSession != null) {
                userSessionRepository.delete(userSession.getId());
            }

            URI url = UriComponentsBuilder.fromHttpUrl(FACEBOOK_TOKEN_VALIDATION_URL)
                    .queryParam("access_token", userLoginInfo.getAccessToken())
                    .queryParam("appsecret_proof", encode(FACEBOOK_APPSECRET, userLoginInfo.getAccessToken()))
                    .build().encode().toUri();

            CompletableFuture<FacebookTokenValidationResponse> responseCompletableFuture
                    = CompletableFuture.supplyAsync(() -> validateFacebookToken(url));

            return responseCompletableFuture.thenApplyAsync(facebookResponse
                    -> validateAndRespondWithUserDetails(facebookResponse.getId(),
                    facebookResponse.getTemporaryUsername(userLoginInfo.getAuthenticationType()), userLoginInfo));
        }
    }

    @Override
    public UserSession getUserSessionForSessionId(String sessionId) {
        return userSessionRepository.findBySessionId(sessionId);
    }

    private UserDetails validateAndRespondWithUserDetails(String userIdFromFacebook, String temporaryUsername, UserLoginInfo userLoginInfo) {
        String authUserId = userLoginInfo.getUserId();
        if (userIdFromFacebook == null || !userIdFromFacebook.equals(authUserId)) {
            throw new IllegalArgumentException("incorrect login details");
        } else {
            User user = userRepository.findByAuthTypeAndAuthId(AuthenticationType.getByType(userLoginInfo.getAuthenticationType()), authUserId);
            Timestamp currentTime = new Timestamp((new Date()).getTime());
            boolean isNewUser = user == null;
            if (isNewUser) {
                user = userRepository.save(new User(temporaryUsername, userLoginInfo.getAuthenticationType(), authUserId, currentTime));
            } else {
                userRepository.updateLastLoginTime(user.getId(), currentTime);
            }
            Calendar expiryCalendar = new GregorianCalendar();
            expiryCalendar.setTime(new Date(currentTime.getTime()));
            expiryCalendar.add(GregorianCalendar.MILLISECOND, Math.toIntExact(SESSION_EXPIRY_IN_MS));
            UserSession userSession;
            try {
                userSession = userSessionRepository.save(new UserSession(user.getId(), userLoginInfo.getAuthenticationType(),
                        userLoginInfo.getUserId(), userLoginInfo.getAccessToken(), expiryCalendar.getTime()));
                return new UserDetails(user.getId(), userSession.getSessionId(), user.getUsername(), !isNewUser);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private FacebookTokenValidationResponse validateFacebookToken(URI url) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<FacebookTokenValidationResponse> response = restTemplate.exchange(url, HttpMethod.GET, entity, FacebookTokenValidationResponse.class);
        return response.getBody();
    }

    private String encode(String key, String data) throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException {
        Mac sha256Hmac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA256");
        sha256Hmac.init(secretKey);
        return Hex.encodeHexString(sha256Hmac.doFinal(data.getBytes("UTF-8")));
    }

    private UserDetails getUserDetailsForExistingUser(UserSession userSession, Long time) {
        Long userId = userSession.getUserId();
        User user = userRepository.findOne(userId);
        user.setLastLogin(new Timestamp(time));
        userRepository.save(user);
        return new UserDetails(userId, userSession.getSessionId(), user.getUsername(), true);
    }
}
