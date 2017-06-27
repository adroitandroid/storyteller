package com.adroitandroid.model;

/**
 * Created by pv on 02/12/16.
 */
public class FacebookTokenValidationResponse {
    private String id;
    private Phone phone;

    public String getId() {
        return id;
    }

    public static class Phone {
        private String number;

        public String getNumber() {
            return number;
        }
    }
}
