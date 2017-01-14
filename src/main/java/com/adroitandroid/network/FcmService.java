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
            "Authorization: key=AAAA8irIdMQ:APA91bGekfF6QKfgG00vzjs6F2vtG1qZ_h_vhNm8jCjE-11Tp9T_1fQUwcWlNx9DJoOsGoGmvgOApWKgqqcR_j7FFn6R5wjsZO9DBq8MklOgVU4iLQZADIEZeYGhU5tcTI25xdll5Gww"
    })
    Call<FcmResponse> sendPush(@Body FcmPushBody fcmPushBody);
}
