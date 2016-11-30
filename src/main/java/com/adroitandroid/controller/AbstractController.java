package com.adroitandroid.controller;

import com.adroitandroid.GsonExclusionStrategy;
import com.adroitandroid.HibernateProxyTypeAdapter;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

/**
 * Created by pv on 30/11/16.
 */
public class AbstractController {

    protected JsonElement prepareResponseFrom(Object src, String... includeAnnotated) {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapterFactory(HibernateProxyTypeAdapter.FACTORY);
        return builder.setExclusionStrategies(new GsonExclusionStrategy(includeAnnotated)).create().toJsonTree(src);
    }
}
