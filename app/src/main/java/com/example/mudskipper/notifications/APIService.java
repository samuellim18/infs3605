package com.example.mudskipper.notifications;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAA7IjWf58:APA91bHOrNvYjlny2ZlQ4g3nu7laX-v8-Q9p0o7pV2R-thMHkfZXsrKsLR3NEvXBcFqcHZQwb8bBd-rmba9IxXQBChZYSW2HD9PTvbuPCFf7X_vc3ytGUDrD-_Bhf0dVpaZAp5tD4vlp"

            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
