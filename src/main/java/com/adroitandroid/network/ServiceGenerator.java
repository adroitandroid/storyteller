package com.adroitandroid.network;

import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by pv on 07/12/16.
 */
public class ServiceGenerator {

    private static final String FCM_PUSH_URL = "https://fcm.googleapis.com";

    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(FCM_PUSH_URL)
                    .addConverterFactory(GsonConverterFactory.create());
    private static OkHttpClient anonymousClient = new OkHttpClient.Builder().build();

    @Value("${fcm.server.key}")
    private String FCM_SERVER_KEY;

    private static Retrofit retrofit = builder.client(anonymousClient).build();
    private static FcmService fcmService = retrofit.create(FcmService.class);

    public static FcmService getFcmService() {
        return fcmService;
    }
}
