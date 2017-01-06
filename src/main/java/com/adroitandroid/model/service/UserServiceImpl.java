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
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Created by pv on 02/12/16.
 */
@Component("userService")
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserStoryRelationRepository userStoryRelationRepository;
    private final StoryStatsRepository storyStatsRepository;
    private final UserChapterRelationRepository userChapterRelationRepository;
    private final ChapterStatsRepository chapterStatsRepository;
    private final UserSessionRepository userSessionRepository;
    private final UserDetailRepository userDetailsRepository;
    private final UserBookmarkRepository userBookmarkRepository;

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



    public UserServiceImpl(UserRepository userRepository,
                           UserStoryRelationRepository userStoryRelationRepository,
                           StoryStatsRepository storyStatsRepository,
                           UserChapterRelationRepository userChapterRelationRepository,
                           ChapterStatsRepository chapterStatsRepository,
                           UserSessionRepository userSessionRepository,
                           UserDetailRepository userDetailsRepository,
                           UserBookmarkRepository userBookmarkRepository) {
        this.userRepository = userRepository;
        this.userStoryRelationRepository = userStoryRelationRepository;
        this.storyStatsRepository = storyStatsRepository;
        this.userChapterRelationRepository = userChapterRelationRepository;
        this.chapterStatsRepository = chapterStatsRepository;
        this.userSessionRepository = userSessionRepository;
        this.userDetailsRepository = userDetailsRepository;
        this.userBookmarkRepository = userBookmarkRepository;
    }

    @Override
    public int setUsername(Long userId, String username) {
        return userRepository.setUsername(userId, username);
    }

    @Override
    public int setLiked(Long userId, Long storyId) {
        int result = userStoryRelationRepository.insertOnDuplicateKeyUpdateSoftDeletedToFalse(
                userId, storyId, UserStoryRelation.RELATION_ID_LIKE, getCurrentTime());
        storyStatsRepository.incrementLikes(storyId);
        return result;
    }

    @Override
    public int unsetLiked(Long userId, Long storyId) {
        int result = userStoryRelationRepository.softDelete(userId, storyId, UserStoryRelation.RELATION_ID_LIKE, getCurrentTime());
        storyStatsRepository.decrementLikes(storyId);
        return result;
    }

    @Override
    public List<UserStoryRelation> getUserLikesSortedByRecentFirst(Long userId) {
        return userStoryRelationRepository.findByUserIdAndRelationIdAndSoftDeletedFalseOrderByUpdatedAtDesc(
                userId, UserStoryRelation.RELATION_ID_LIKE);
    }

    @Override
    public int setToReadLater(Long userId, Long storyId) {
        return userStoryRelationRepository.insertOnDuplicateKeyUpdateSoftDeletedToFalse(
                userId, storyId, UserStoryRelation.RELATION_ID_READ_LATER, getCurrentTime());
    }

    @Override
    public int removeFromReadLater(Long userId, Long storyId) {
        return userStoryRelationRepository.softDelete(userId, storyId, UserStoryRelation.RELATION_ID_READ_LATER, getCurrentTime());
    }

    @Override
    public List<UserStoryRelation> getUserReadLaterSortedByRecentFirst(Long userId) {
        return userStoryRelationRepository.findByUserIdAndRelationIdAndSoftDeletedFalseOrderByUpdatedAtDesc(
                userId, UserStoryRelation.RELATION_ID_READ_LATER);
    }

    @Override
    public int setBookmark(Long userId, Long chapterId) {
        int result = userChapterRelationRepository.insertOnDuplicateKeyUpdateSoftDeletedToFalse(
                userId, chapterId, UserChapterRelation.RELATION_ID_BOOKMARK, getCurrentTime());
        chapterStatsRepository.incrementBookmarks(chapterId);
        return result;
    }

    @Override
    public int removeBookmark(Long userId, Long chapterId) {
        int result = userChapterRelationRepository.softDelete(
                userId, chapterId, UserChapterRelation.RELATION_ID_BOOKMARK, getCurrentTime());
        chapterStatsRepository.decrementBookmarks(chapterId);
        return result;
    }

    @Override
    public List<UserChapterRelation> getUserBookmarksSortedByRecentFirst(Long userId) {
        return userChapterRelationRepository.findByUserIdAndRelationIdAndSoftDeletedFalseOrderByUpdatedAtDesc(
                userId, UserStoryRelation.RELATION_ID_LIKE);
    }

    @Override
    public CompletableFuture<UserLoginDetails> signIn(UserLoginInfo userLoginInfo)
            throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {
        Date currentTime = new Date();
        UserSession userSession = userSessionRepository.findByAuthTypeAndAuthUserIdAndAccessToken(
                userLoginInfo.getAuthenticationType(), userLoginInfo.getAuthUserId(), userLoginInfo.getAccessToken());
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
                    -> validateAndRespondWithUserDetails(facebookResponse.getId(), userLoginInfo));
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
    public List<UserChapterRelation> getUserBookmarksFromChapters(Long userId, List<Chapter> chapters) {
        ArrayList<Long> chapterIdList = new ArrayList<>();
        for (Chapter chapter : chapters) {
            chapterIdList.add(chapter.getId());
        }
        if (!chapterIdList.isEmpty()) {
            return userChapterRelationRepository.findByUserIdAndChapterIdInAndRelationIdAndSoftDeletedFalse(
                    userId, chapterIdList, UserChapterRelation.RELATION_ID_BOOKMARK);
        } else {
            return null;
        }
    }

    @Override
    public boolean hasUserLikedStory(Long userId, Long storyId) {
        UserStoryRelation userStoryRelation
                = userStoryRelationRepository.findByUserIdAndStoryIdAndRelationIdAndSoftDeletedFalse(
                        userId, storyId, UserStoryRelation.RELATION_ID_LIKE);
        return userStoryRelation != null;
    }

    @Override
    public int updateToken(Long userId, String fcmToken) {
        if (userId > 0) {
            return userDetailsRepository.updateToken(userId, fcmToken, getCurrentTime());
        } else {
            return 0;
        }
    }

    @Override
    public void updateUserBookmark(UserBookmark userBookmark) {
         userBookmarkRepository.update(userBookmark.getUserId(),
                 userBookmark.getSnippet().getId(), userBookmark.getSoftDeleted() ? 1 : 0);
    }

    private UserLoginDetails validateAndRespondWithUserDetails(String userIdFromFacebook, UserLoginInfo userLoginInfo) {
        String authUserId = userLoginInfo.getAuthUserId();
        if (userIdFromFacebook == null || !userIdFromFacebook.equals(authUserId)) {
            throw new IllegalArgumentException("incorrect login details");
        } else {
            User user = userRepository.findByAuthTypeAndAuthUserId(AuthenticationType.getByType(userLoginInfo.getAuthenticationType()), authUserId);
            boolean isNewUser = user == null;
            Timestamp currentTime = new Timestamp((new Date()).getTime());
            if (isNewUser) {
                user = userRepository.save(new User(userLoginInfo.getAuthenticationType(), authUserId));
            } else {
                userRepository.updateLastActiveTime(user.getId(), currentTime);
            }
            try {
                UserSession userSession = userSessionRepository.save(new UserSession(user.getId(), userLoginInfo.getAuthenticationType(),
                        userLoginInfo.getAuthUserId(), userLoginInfo.getAccessToken(), new Date(currentTime.getTime())));
                String token = encodeAuthToken(userSession.getCreationTime(), userSession.getSessionId());
                return new UserLoginDetails(user.getId(), token, user.getUsername());
            } catch (NoSuchAlgorithmException | JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return null;
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

    private UserLoginDetails getUserDetailsForExistingUser(UserSession userSession, Long time) {
        Long userId = userSession.getUserId();
        User user = userRepository.findOne(userId);
        user.setLastActiveAt(new Timestamp(time));
        userRepository.save(user);
        try {
            String token = encodeAuthToken(userSession.getCreationTime(), userSession.getSessionId());
            return new UserLoginDetails(userId, token, user.getUsername());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
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

    private Timestamp getCurrentTime() {
        return new Timestamp((new Date()).getTime());
    }
}
