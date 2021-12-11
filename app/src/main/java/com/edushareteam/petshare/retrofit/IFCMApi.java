package com.edushareteam.petshare.retrofit;



import com.edushareteam.petshare.models.FCMBody;
import com.edushareteam.petshare.models.FCMResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMApi {

    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAqzpn0nc:APA91bHb-X08ASkebIxb_iKXH1WuBZQqrXv5s8r82PCNaLYd0-mCij2Vg6ueTz3hF_b-kUTVytnKyJWBzSriX7nIsjMWwJrHscVGEMJUlD_tksbWjVcJWQ62w7_9MZZpj_undrFYKaVQ"
    })
    @POST("fcm/send")
    Call<FCMResponse> send(@Body FCMBody body);
}
