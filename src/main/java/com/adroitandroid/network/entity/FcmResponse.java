package com.adroitandroid.network.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by pv on 07/12/16.
 */

/**
 * As per docs at https://firebase.google.com/docs/cloud-messaging/server
 */
public class FcmResponse {
    private int success;
    private int failure;
    private List<FcmResult> results;

    public List<FcmResult> getResults() {
        return results;
    }

    public static class FcmResult {
        private static final String ERROR_UNAVAILABLE = "Unavailable";
        private static final String ERROR_NOT_REGISTERED = "NotRegistered";


        @SerializedName(value = "message_id")
        private String messageId;

        private String error;

        @SerializedName(value = "registration_id")
        private String registrationId;

        public boolean isSuccess() {
            return error == null;
        }

        public String getRegistrationId() {
            return registrationId;
        }

        public boolean shouldResend() {
            return ERROR_UNAVAILABLE.equals(error);
        }

        public boolean shouldRemove() {
            return ERROR_NOT_REGISTERED.equals(error);
        }

        public boolean isUnrecoverableError() {
            return !ERROR_NOT_REGISTERED.equals(error) && !ERROR_UNAVAILABLE.equals(error);
        }
    }
}
