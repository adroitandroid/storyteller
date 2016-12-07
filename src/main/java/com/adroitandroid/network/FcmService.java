package com.adroitandroid.network;

import com.adroitandroid.network.entity.FcmPushBody;
import com.adroitandroid.network.entity.FcmResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by pv on 07/12/16.
 */
public interface FcmService {
    @POST("/fcm/send")
    @Headers({
            "Accept: application/json",
            "Authorization: key=AAAAatGOMVQ:APA91bH9Tw8MfSsuL2HUW2XtflC8vy55rKDjtkgKQFH67G1lj4CA-rngARd68LkasN0Y-e8tLPtroqsRfUlgAWeUvsvN3Va4mND-i8_azUvWL99wgUgBo8Z-HmtRrVuCgKh04h5KmO69mw-hj32upz5UxG3hPE5IAw"
    })
    Call<FcmResponse> sendPush(@Body FcmPushBody fcmPushBody);
}
