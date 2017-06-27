package com.adroitandroid.controller;

import com.adroitandroid.serializer.GsonExclusionStrategy;
import com.adroitandroid.serializer.HibernateProxyTypeAdapter;
import com.adroitandroid.security.DemoAuthenticationToken;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by pv on 30/11/16.
 */
@RestController
class AbstractController {

    public static final String INVALID_USER_MESSAGE = "bad user";

    JsonElement prepareResponseFrom(Object src, String... includeAnnotated) {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapterFactory(HibernateProxyTypeAdapter.FACTORY);
        return builder.setExclusionStrategies(new GsonExclusionStrategy(includeAnnotated)).create().toJsonTree(src);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    void handleBadRequests(HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * For optional check of userId
     * @return
     */
    Long getUserIdFromRequest() {
        return (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    /**
     * For mandatory requirement of userId
     * @return
     */
    Long needUserId() {
        Long userIdFromRequest = getUserIdFromRequest();
        if (userIdFromRequest == null || userIdFromRequest < 0) {
            throw new IllegalArgumentException(INVALID_USER_MESSAGE);
        }
        return userIdFromRequest;
    }

    void returnIfUnauthenticatedUserSession() {
        if (DemoAuthenticationToken.isUnauthenticatedUser(getUserIdFromRequest())) {
            throw new IllegalArgumentException(INVALID_USER_MESSAGE);
        }
    }
}
