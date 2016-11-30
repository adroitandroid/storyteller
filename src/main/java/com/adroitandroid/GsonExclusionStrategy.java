package com.adroitandroid;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Created by pv on 30/11/16.
 */
public class GsonExclusionStrategy implements ExclusionStrategy {


    private final List<String> excludeInAnnotation;

    public GsonExclusionStrategy(String... annotationValuesToExclude) {
        this.excludeInAnnotation = Arrays.asList(annotationValuesToExclude);
    }

    @Override
    public boolean shouldSkipClass(Class<?> clazz) {
        return false;
    }

    @Override
    public boolean shouldSkipField(FieldAttributes f) {
        OptionalInGson annotation = f.getAnnotation(OptionalInGson.class);
        return annotation != null && excludeInAnnotation != null && excludeInAnnotation.contains(annotation.exclude());
    }
}
