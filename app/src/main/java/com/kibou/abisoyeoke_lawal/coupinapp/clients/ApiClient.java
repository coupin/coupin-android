package com.kibou.abisoyeoke_lawal.coupinapp.clients;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kibou.abisoyeoke_lawal.coupinapp.BuildConfig;
import com.kibou.abisoyeoke_lawal.coupinapp.R;
import com.kibou.abisoyeoke_lawal.coupinapp.interfaces.ApiCalls;
import com.kibou.abisoyeoke_lawal.coupinapp.utils.PreferenceManager;

import java.lang.annotation.Annotation;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final ApiClient apiClient = null;
    private static Retrofit retrofit = null;

    public ApiClient() {
    }

    public static ApiClient getInstance() {
        return apiClient == null ? new ApiClient() : apiClient;
    }

    public static ApiError parseError(retrofit2.Response<?> response) {
        ApiError error;

        try {
            Converter<ResponseBody, ApiError> converter = retrofit.responseBodyConverter(
                    ApiError.class, new Annotation[0]
            );

            error = converter.convert(response.errorBody());
            assert error != null;
            error.statusCode = response.code();
        } catch (Exception e) {
            e.printStackTrace();
            return new ApiError();
        }

        return error;
    }

    private void setRetrofit(Context context, boolean update) {
        if (retrofit == null || update) {
            String token = PreferenceManager.getToken();
            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
//            if (BuildConfig.BUILD_TYPE.equals("debug")) {
                httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//            } else {
//                httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
//            }
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(chain -> {
                        Request.Builder builder = chain.request().newBuilder();
                        if (token != null) builder.addHeader("Authorization", token);
                        Request request = builder.build();
                        return chain.proceed(request);
                    })
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .callTimeout(60, TimeUnit.SECONDS)
                    .addInterceptor(httpLoggingInterceptor)
                    .build();
            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                    .create();

            retrofit = new Retrofit.Builder()
                    .baseUrl(context.getResources().getString(R.string.base_url))
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(client)
                    .build();
        }
    }

    private Retrofit getRetrofit(Context context, boolean update) {
        setRetrofit(context, update);
        return retrofit;
    }

    public ApiCalls getCalls(Context context, boolean update) {
        return getRetrofit(context, update).create(ApiCalls.class);
    }
}
