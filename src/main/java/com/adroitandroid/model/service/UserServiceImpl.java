package com.adroitandroid.model.service;

import com.adroitandroid.model.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.lang.reflect.Type;
import java.net.URI;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

/**
 * Created by pv on 02/12/16.
 */
@Component("userService")
@Transactional
public class UserServiceImpl extends AbstractService implements UserService {
    private final UserRepository userRepository;
    private final UserSessionRepository userSessionRepository;
    private final UserDetailRepository userDetailsRepository;
    private final UserBookmarkRepository userBookmarkRepository;
    private final SnippetRepository snippetRepository;
    private final UserRelationRepository userRelationsRepository;
    private final UserSnippetVoteRepository userSnippetVoteRepository;
    private final UserStatsRepository userStatsRepository;
    private final UserStatusRepository userStatusRepository;

    @Value("${facebook.uservalidation.url}")
    private String FACEBOOK_TOKEN_VALIDATION_URL;
    @Value("${facebook.appsecret}")
    private String FACEBOOK_APPSECRET;
    @Value("${google.oauth.clientid.web}")
    private String GOOGLE_OAUTH_WEB_CLIENT_ID;
    @Value("${firebase.database.url}")
    private String FIREBASE_DATABASE_URL;

    private static final RestTemplate restTemplate;

    static {
        restTemplate = new RestTemplate();
        SimpleClientHttpRequestFactory rf = (SimpleClientHttpRequestFactory) restTemplate.getRequestFactory();
        rf.setConnectTimeout(10 * 1000);
        rf.setReadTimeout(10 * 1000);
    }

    public UserServiceImpl(UserRepository userRepository,
                           UserSessionRepository userSessionRepository,
                           UserDetailRepository userDetailsRepository,
                           UserBookmarkRepository userBookmarkRepository,
                           SnippetRepository snippetRepository,
                           UserRelationRepository userRelationsRepository,
                           UserSnippetVoteRepository userSnippetVoteRepository,
                           UserStatsRepository userStatsRepository,
                           UserStatusRepository userStatusRepository) {
        this.userRepository = userRepository;
        this.userSessionRepository = userSessionRepository;
        this.userDetailsRepository = userDetailsRepository;
        this.userBookmarkRepository = userBookmarkRepository;
        this.snippetRepository = snippetRepository;
        this.userRelationsRepository = userRelationsRepository;
        this.userSnippetVoteRepository = userSnippetVoteRepository;
        this.userStatsRepository = userStatsRepository;
        this.userStatusRepository = userStatusRepository;
    }

    @PostConstruct
    public void init() throws FileNotFoundException {
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setServiceAccount(new FileInputStream("src/main/resources/weave-bc0fd-firebase-adminsdk-2q0fc-f473110538.json"))
                .setDatabaseUrl(FIREBASE_DATABASE_URL)
                .build();

        FirebaseApp.initializeApp(options);
    }

    @Override
    public CompletableFuture<UserLoginDetails> signIn(UserLoginInfo userLoginInfo)
            throws GeneralSecurityException, IOException, InterruptedException {
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

            if (AuthenticationType.PHONE.equals(userLoginInfo.getAuthenticationType())) {
                URI url = UriComponentsBuilder.fromHttpUrl(FACEBOOK_TOKEN_VALIDATION_URL)
                        .queryParam("access_token", userLoginInfo.getAccessToken())
                        .queryParam("appsecret_proof", encodeAppSecretProof(FACEBOOK_APPSECRET, userLoginInfo.getAccessToken()))
                        .build().encode().toUri();

                CompletableFuture<FacebookTokenValidationResponse> responseCompletableFuture
                        = CompletableFuture.supplyAsync(() -> validateFacebookToken(url));

                return responseCompletableFuture.thenApplyAsync(facebookResponse
                        -> validateAndRespondWithUserDetails(facebookResponse.getId(), userLoginInfo,
                        new User(userLoginInfo.getAuthenticationType(), userLoginInfo.getAuthUserId())));
            } else if (AuthenticationType.GOOGLE.equals(userLoginInfo.getAuthenticationType())) {

                final UserLoginDetails[] userLoginDetails = {null};
                CountDownLatch countDownLatch = new CountDownLatch(1);
                FirebaseAuth.getInstance().verifyIdToken(userLoginInfo.getAccessToken())
                        .addOnSuccessListener(decodedToken -> {
                            userLoginDetails[0]
                                    = getUserLoginDetails(userLoginInfo, new User(userLoginInfo.getAuthenticationType(),
                                            userLoginInfo.getAuthUserId(), decodedToken.getName(), decodedToken.getPicture(),
                                            decodedToken.getEmail()));
                            countDownLatch.countDown();
                        })
                        .addOnFailureListener(e -> {
                            throw new IllegalArgumentException("incorrect login details " + e.getLocalizedMessage());
                        });
                countDownLatch.await();

                return CompletableFuture.supplyAsync(() -> userLoginDetails[0]);
            }
        }
        throw new IllegalArgumentException("incorrect login details");
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

    @Override
    public List<SnippetListItemForUpdate> getUpdatesFor(Long userId) {
        User user = userRepository.findOne(userId);
        Timestamp lastActiveTime = user.getLastActiveAt();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, -1);
        Timestamp yesterday = new Timestamp(calendar.getTime().getTime());
        Timestamp updatesSince = lastActiveTime.after(yesterday) ? yesterday : lastActiveTime;

        List<ContributionUpdate> contributionUpdateList = snippetRepository.getNewContributionsFromFollowed(userId, updatesSince);
        Map<Long, SnippetListItemForUpdate> itemForUpdateMap = getSnippetsMapFor(contributionUpdateList);

        List<VoteUpdate> voteUpdateList = snippetRepository.getVoteUpdatesOnContributionsBy(userId, updatesSince);
        List<ChildUpdate> childUpdateList = snippetRepository.getChildUpdatesOnContributionsBy(userId, updatesSince);
        itemForUpdateMap.putAll(getSnippetsMapFor(voteUpdateList, childUpdateList));

        voteUpdateList = snippetRepository.getVoteUpdatesOnBookmarksFor(userId, updatesSince);
        childUpdateList = snippetRepository.getChildUpdatesOnBookmarksFor(userId, updatesSince);
        itemForUpdateMap.putAll(getSnippetsMapFor(voteUpdateList, childUpdateList));

        for (SnippetListItemForUpdate update : itemForUpdateMap.values()) {
            update.setCategoryFromUpdate();
        }
        ArrayList<SnippetListItemForUpdate> updates = new ArrayList<>(itemForUpdateMap.values());
        updates.sort((o1, o2) -> o2.getLastUpdateAt().compareTo(o1.getLastUpdateAt()));

        ArrayList<SnippetListItem> updateListItems = new ArrayList<>(updates);
        updateBookmarkAndVoteStatusFor(userId, updateListItems, false);

        Type listType = new TypeToken<ArrayList<SnippetListItemForUpdate>>() {}.getType();
        JsonElement jsonElement = prepareResponseFrom(updateListItems);
        return new Gson().fromJson(jsonElement, listType);
    }

    private Map<Long, SnippetListItemForUpdate> getSnippetsMapFor(List<ContributionUpdate> contributionUpdateList) {
        Map<Long, SnippetListItemForUpdate> itemForUpdateMap = new HashMap<>();
        for (ContributionUpdate contributionUpdate : contributionUpdateList) {
            Long snippetId = contributionUpdate.getSnippet().getId();
            SnippetListItemForUpdate snippetListItemForUpdate = itemForUpdateMap.get(snippetId);
            if (snippetListItemForUpdate == null) { // not adding those that are already added as of interest
                snippetListItemForUpdate = new SnippetListItemForUpdate(contributionUpdate.getSnippet(),
                        contributionUpdate.getParentSnippet(), contributionUpdate.getSnippetStats(),
                        contributionUpdate.getUser(), contributionUpdate.getCreatedTime(), true);
                itemForUpdateMap.put(snippetListItemForUpdate.getSnippetId(), snippetListItemForUpdate);
            }
        }
        return itemForUpdateMap;
    }

    @Override
    public void updateFollowRelationship(Long followedUserId, Long followerUserId, boolean unfollow) {
        userRelationsRepository.updateFollow(followedUserId, followerUserId, unfollow, getCurrentTime());
        userStatsRepository.updateFollowersCount(followedUserId, unfollow ? -1 : 1);
    }

    @Override
    public List<SnippetListItem> getAllBookmarksOf(Long userId) {
        Type listType = new TypeToken<ArrayList<SnippetListItem>>() {}.getType();
        List<SnippetListItem> allBookmarks = userBookmarkRepository.findAllBookmarksFor(userId);

        updateBookmarkAndVoteStatusFor(userId, allBookmarks, true);

        JsonElement jsonElement = prepareResponseFrom(allBookmarks);
        return new Gson().fromJson(jsonElement, listType);
    }

    @Override
    public List<UserStatus> getStatusFor(Long userId) {
        return userStatusRepository.findByUserId(userId);
    }

    @Override
    public void updateStatus(UserStatus userStatus) {
        UserStatus statusInDb = userStatusRepository.findByUserIdAndEvent(userStatus.getUserId(), userStatus.getEvent());
        if ((statusInDb == null || statusInDb.getStatus() == 0) && userStatus.getStatus() == 1) {
            if (statusInDb != null) {
                userStatus = statusInDb;
                userStatus.setStatus(1);
            }
            userStatus.setUpdatedAt(getCurrentTime());
            userStatusRepository.save(userStatus);
        }
    }

    @Override
    public UserProfile getProfileFor(Long userId, Long requestingUserId) {
        User user = userRepository.findOne(userId);
        Type listType = new TypeToken<ArrayList<SnippetListItem>>() {}.getType();
        JsonElement jsonElement = prepareResponseFrom(snippetRepository.getContributionsBy(userId));
        List<SnippetListItem> contributions = new Gson().fromJson(jsonElement, listType);

        UserRelation relation = null;
        if (requestingUserId != null) {
            updateBookmarkAndVoteStatusFor(requestingUserId, contributions, userId.equals(requestingUserId));
            relation = userRelationsRepository.findByUserIdAndFollowerUserIdAndSoftDeletedFalse(userId, requestingUserId);
        }
        User userWithFetch = new Gson().fromJson(prepareResponseFrom(user, User.USER_STATS_IN_USER), User.class);
        return new UserProfile(userWithFetch, contributions, relation != null);
    }

    @Override
    public void update(User user) {
        User userInDb = userRepository.findOne(user.getId());
        userInDb.setUsername(user.getUsername());
        userInDb.setDescription(user.getDescription());
        userRepository.save(userInDb);
    }

    private Map<Long, SnippetListItemForUpdate> getSnippetsMapFor(List<VoteUpdate> voteUpdateList,
                                                                  List<ChildUpdate> childUpdateList) {
        Map<Long, SnippetListItemForUpdate> itemForUpdateMap = new HashMap<>();
        for (VoteUpdate voteUpdate : voteUpdateList) {
            SnippetListItemForUpdate snippetListItemForUpdate = new SnippetListItemForUpdate();
            snippetListItemForUpdate.setVotes(voteUpdate);
            itemForUpdateMap.put(snippetListItemForUpdate.getSnippetId(), snippetListItemForUpdate);
        }
        for (ChildUpdate childUpdate : childUpdateList) {
            Long snippetId = childUpdate.getSnippet().getId();
            SnippetListItemForUpdate snippetListItemForUpdate = itemForUpdateMap.get(snippetId);
            if (snippetListItemForUpdate == null) {
                snippetListItemForUpdate = new SnippetListItemForUpdate();
            }
            snippetListItemForUpdate.setChildInfo(childUpdate);
            itemForUpdateMap.put(snippetListItemForUpdate.getSnippetId(), snippetListItemForUpdate);
        }
        return itemForUpdateMap;
    }

//    ----------------------------- vver methods end ---------------------

    private UserLoginDetails validateAndRespondWithUserDetails(String userIdFromValidationOnServer,
                                                               UserLoginInfo userLoginInfo,
                                                               User newUser) {
        String authUserId = userLoginInfo.getAuthUserId();
        if (userIdFromValidationOnServer == null || !userIdFromValidationOnServer.equals(authUserId)) {
            throw new IllegalArgumentException("incorrect login details");
        } else {
            return getUserLoginDetails(userLoginInfo, newUser);
        }
    }

    private UserLoginDetails getUserLoginDetails(UserLoginInfo userLoginInfo, User newUser) {
        User user = userRepository.findByAuthTypeAndAuthUserId(
                AuthenticationType.getByType(userLoginInfo.getAuthenticationType()), userLoginInfo.getAuthUserId());
        boolean isNewUser = user == null;
        Timestamp currentTime = new Timestamp((new Date()).getTime());
        if (isNewUser) {
            user = userRepository.save(newUser);
            userStatusRepository.save(UserStatus.getInitialSnippetsStatus(user.getId(), currentTime));
            userStatusRepository.save(UserStatus.getEligibleToAddSnippetsStatus(user.getId(), currentTime));
        } else {
            userRepository.updateLastActiveTime(user.getId(), currentTime);
        }
        try {
            userSessionRepository.removeByUserId(user.getId());
            UserSession userSession = userSessionRepository.save(new UserSession(user.getId(), userLoginInfo.getAuthenticationType(),
                    userLoginInfo.getAuthUserId(), userLoginInfo.getAccessToken(), new Date(currentTime.getTime())));
            String token = encodeAuthToken(userSession.getCreationTime(), userSession.getSessionId());
            return new UserLoginDetails(user.getId(), token, user.getUsername());
        } catch (NoSuchAlgorithmException | JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
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

    @Override
    protected UserBookmarkRepository getUserBookmarkRepository() {
        return userBookmarkRepository;
    }

    @Override
    protected UserSnippetVoteRepository getUserSnippetVoteRepository() {
        return userSnippetVoteRepository;
    }
}
