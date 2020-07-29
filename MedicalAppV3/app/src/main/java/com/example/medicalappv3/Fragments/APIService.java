package com.example.medicalappv3.Fragments;

import com.example.medicalappv3.UserDefinedClasses.Notifications.MyResponse;
import com.example.medicalappv3.UserDefinedClasses.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Authorization: key=AAAAJy2vM28:APA91bEkrOBC39eACrKg7KeqbTo48gD6SfvJI789WTdWsxhxiy5xxMbDVrBSA31j2pwCvb1JUwAzffjkm5NW529YpQJB2omK-1uCNXmFU20anbJ360JGquSESz9RNxo1KexDxoHTb-pj",
                    "Content-Type:application/json"
            }
    )
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
