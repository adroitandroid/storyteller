package com.adroitandroid.controller;

import com.adroitandroid.GsonExclusionStrategy;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

/**
 * Created by pv on 30/11/16.
 */
public class AbstractController {

    protected JsonElement prepareResponseFrom(Object src, String... excludeAnnotations) {
        return new GsonBuilder().setExclusionStrategies(new GsonExclusionStrategy(excludeAnnotations)).create().toJsonTree(src);
    }
}
