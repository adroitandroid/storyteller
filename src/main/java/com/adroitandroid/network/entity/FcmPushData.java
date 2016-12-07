package com.adroitandroid.network.entity;

/**
 * Created by pv on 07/12/16.
 */
public class FcmPushData {
    private final String title;
    private final String body;
    private final int type;

    public FcmPushData(String title, String body, int type) {
        this.title = title;
        this.body = body;
        this.type = type;
    }
}
