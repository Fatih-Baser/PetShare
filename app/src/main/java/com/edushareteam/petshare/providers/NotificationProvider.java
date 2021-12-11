package com.edushareteam.petshare.providers;


import com.edushareteam.petshare.models.FCMBody;
import com.edushareteam.petshare.models.FCMResponse;
import com.edushareteam.petshare.retrofit.IFCMApi;
import com.edushareteam.petshare.retrofit.RetrofitClient;

import retrofit2.Call;

public class NotificationProvider {

    private String url = "https://fcm.googleapis.com";

    public NotificationProvider() {

    }

    public Call<FCMResponse> sendNotification(FCMBody body) {
        return RetrofitClient.getClient(url).create(IFCMApi.class).send(body);
    }
}
