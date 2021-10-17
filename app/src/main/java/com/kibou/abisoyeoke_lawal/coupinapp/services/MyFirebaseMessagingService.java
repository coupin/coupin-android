package com.kibou.abisoyeoke_lawal.coupinapp.services;

import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.kibou.abisoyeoke_lawal.coupinapp.clients.ApiClient;
import com.kibou.abisoyeoke_lawal.coupinapp.clients.ApiError;
import com.kibou.abisoyeoke_lawal.coupinapp.interfaces.ApiCalls;
import com.kibou.abisoyeoke_lawal.coupinapp.models.requests.TokenRequest;
import com.kibou.abisoyeoke_lawal.coupinapp.models.responses.GenericResponse;
import com.kibou.abisoyeoke_lawal.coupinapp.utils.PreferenceManager;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private ApiCalls apiCalls;

    @Override
    public void onMessageReceived(RemoteMessage message) { super.onMessageReceived(message); }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        PreferenceManager.setContext(getApplicationContext());
        apiCalls = ApiClient.getInstance().getCalls(getApplicationContext(), true);

        sendToServer(token);
    }

    private void sendToServer(final String token) {
        Call<GenericResponse> request = apiCalls.setNotificationToken(PreferenceManager.getUserId(), new TokenRequest(token));
        request.enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                if (!response.isSuccessful()) {
                    ApiError error = ApiClient.parseError(response);
                    Toast.makeText(
                            MyFirebaseMessagingService.this,
                            error.message,
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(
                        MyFirebaseMessagingService.this,
                        "Failed to update notification id.",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }
}
