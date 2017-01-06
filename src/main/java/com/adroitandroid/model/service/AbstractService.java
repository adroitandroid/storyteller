package com.adroitandroid.model.service;

import com.adroitandroid.serializer.GsonExclusionStrategy;
import com.adroitandroid.serializer.HibernateProxyTypeAdapter;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by pv on 05/01/17.
 */
@Service
@Transactional
public class AbstractService {
    JsonElement prepareResponseFrom(Object src, String... includeAnnotated) {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapterFactory(HibernateProxyTypeAdapter.FACTORY);
        return builder.setExclusionStrategies(new GsonExclusionStrategy(includeAnnotated)).create().toJsonTree(src);
    }
}
