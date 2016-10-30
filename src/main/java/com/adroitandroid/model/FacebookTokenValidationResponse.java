package com.adroitandroid.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by pv on 29/10/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FacebookTokenValidationResponse {
    private String id;
    private Phone phone;

    public String getId() {
        return id;
    }

    public Phone getPhone() {
        return phone;
    }

    public String getTemporaryUsername(String authType) {
        switch (authType) {
            case AuthenticationType.PHONE:
                return getPhone().getNumber();
        }
        return null;
    }

    public static class Phone {
        private String number;

        public String getNumber() {
            return number;
        }
    }
}
