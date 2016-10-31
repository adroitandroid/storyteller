package com.adroitandroid.model.service;

import com.adroitandroid.model.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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
        if (userSession != null && !withinSessionRecreationWindow(userSession, currentTime)) {
            return CompletableFuture.supplyAsync(() -> getUserDetailsForExistingUser(userSession, currentTime.getTime()));
        } else {
//            Delete existing session from cache since session or access token is expired
            if (userSession != null) {
                userSessionRepository.delete(userSession.getId());
            }

            URI url = UriComponentsBuilder.fromHttpUrl(FACEBOOK_TOKEN_VALIDATION_URL)
                    .queryParam("access_token", userLoginInfo.getAccessToken())
                    .queryParam("appsecret_proof", encodeAppSecretProof(FACEBOOK_APPSECRET, userLoginInfo.getAccessToken()))
                    .build().encode().toUri();

            CompletableFuture<FacebookTokenValidationResponse> responseCompletableFuture
                    = CompletableFuture.supplyAsync(() -> validateFacebookToken(url));

            return responseCompletableFuture.thenApplyAsync(facebookResponse
                    -> validateAndRespondWithUserDetails(facebookResponse.getId(),
                    facebookResponse.getTemporaryUsername(userLoginInfo.getAuthenticationType()), userLoginInfo));
        }
    }

    @Override
    public UserSession getUserSessionForAuthToken(String authToken) throws IOException {
        JsonNode jsonNode = decodeAuthToken(authToken);
        if (jsonNode.get("expiryTime").asLong() > (new Date()).getTime()) {
            return userSessionRepository.findBySessionId(jsonNode.get("sessionId").asText());
        } else {
            return null;
        }
    }

    @Override
    public UserDetails changeUsernameFor(Long userId, String newUsername) throws JsonProcessingException {
        UserSession userSession = userSessionRepository.findByUserId(userId);
        String token = encodeAuthToken(userSession.getCreationTime(), userSession.getSessionId());
        int updateStatus = userRepository.updateUsername(userId, newUsername);
        if (updateStatus == 0 || updateStatus == 1 || updateStatus == 2) {
            return new UserDetails(userId, token, newUsername, true);
        } else {
            return new UserDetails(userId, token, null, false);
        }
    }

    private boolean withinSessionRecreationWindow(UserSession userSession, Date currentTime) {
        if (userSession == null) {
            return false;
        }
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(userSession.getCreationTime());
        calendar.add(GregorianCalendar.SECOND, UserService.SESSION_EXPIRY_IN_SEC);
        calendar.add(GregorianCalendar.SECOND, -UserService.REGEN_WINDOW_IN_SEC);
        Date sessionRecreationStartTime = calendar.getTime();
        return sessionRecreationStartTime.before(currentTime);
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
                userRepository.updateLastActiveTime(user.getId(), currentTime);
            }
            try {
                UserSession userSession = userSessionRepository.save(new UserSession(user.getId(), userLoginInfo.getAuthenticationType(),
                        userLoginInfo.getUserId(), userLoginInfo.getAccessToken(), new Date(currentTime.getTime())));
                String token = encodeAuthToken(userSession.getCreationTime(), userSession.getSessionId());
                return new UserDetails(user.getId(), token, user.getUsername(), isNewUser ? false : null);
            } catch (NoSuchAlgorithmException | JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

//    encodes auth token to client non-readable value that stores session expiry time as well, so that client can call for new auth token at appropriate times
    private String encodeAuthToken(Date creationTime, String sessionId) throws JsonProcessingException {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(creationTime);
        calendar.add(GregorianCalendar.SECOND, UserService.SESSION_EXPIRY_IN_SEC);

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("expiryTime", calendar.getTimeInMillis());
        objectNode.put("sessionId", sessionId);

        return Base64.getEncoder().encodeToString(mapper.writer().writeValueAsBytes(objectNode));
    }

    private JsonNode decodeAuthToken(String authToken) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectReader reader = mapper.reader();
        return reader.readTree(new ByteArrayInputStream(Base64.getDecoder().decode(authToken)));
    }

    private FacebookTokenValidationResponse validateFacebookToken(URI url) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<FacebookTokenValidationResponse> response = restTemplate.exchange(url, HttpMethod.GET, entity,
                FacebookTokenValidationResponse.class);
        return response.getBody();
    }

    private String encodeAppSecretProof(String key, String data) throws NoSuchAlgorithmException, UnsupportedEncodingException,
            InvalidKeyException {
        Mac sha256Hmac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA256");
        sha256Hmac.init(secretKey);
        return Hex.encodeHexString(sha256Hmac.doFinal(data.getBytes("UTF-8")));
    }

    private UserDetails getUserDetailsForExistingUser(UserSession userSession, Long time) {
        Long userId = userSession.getUserId();
        User user = userRepository.findOne(userId);
        user.setLastActive(new Timestamp(time));
        userRepository.save(user);
        try {
            String token = encodeAuthToken(userSession.getCreationTime(), userSession.getSessionId());
            return new UserDetails(userId, token, user.getUsername(), null);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
