package com.adroitandroid;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

import java.util.Arrays;
import java.util.List;

/**
 * Created by pv on 30/11/16.
 */
public class GsonExclusionStrategy implements ExclusionStrategy {


    private final List<String> includeInAnnotation;

    public GsonExclusionStrategy(String... annotationValuesToInclude) {
        this.includeInAnnotation = Arrays.asList(annotationValuesToInclude);
    }

    @Override
    public boolean shouldSkipClass(Class<?> clazz) {
        return false;
    }

    @Override
    public boolean shouldSkipField(FieldAttributes f) {
        OptionalInGson annotation = f.getAnnotation(OptionalInGson.class);
        return annotation != null && includeInAnnotation != null && !includeInAnnotation.contains(annotation.exclude());
    }
}
