package com.adroitandroid.controller;

import com.adroitandroid.GsonExclusionStrategy;
import com.adroitandroid.HibernateProxyTypeAdapter;
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

    JsonElement prepareResponseFrom(Object src, String... includeAnnotated) {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapterFactory(HibernateProxyTypeAdapter.FACTORY);
        return builder.setExclusionStrategies(new GsonExclusionStrategy(includeAnnotated)).create().toJsonTree(src);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    void handleBadRequests(HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value());
    }


    Long getUserIdFromRequest() {
        return (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
