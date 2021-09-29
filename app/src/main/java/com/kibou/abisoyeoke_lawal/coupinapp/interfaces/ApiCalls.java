package com.kibou.abisoyeoke_lawal.coupinapp.interfaces;

import com.kibou.abisoyeoke_lawal.coupinapp.models.Merchant;
import com.kibou.abisoyeoke_lawal.coupinapp.models.MerchantV2;
import com.kibou.abisoyeoke_lawal.coupinapp.models.Prime;
import com.kibou.abisoyeoke_lawal.coupinapp.models.User;
import com.kibou.abisoyeoke_lawal.coupinapp.models.requests.MerchantRequest;
import com.kibou.abisoyeoke_lawal.coupinapp.models.requests.SignInRequest;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiCalls {
    @POST("signin/c")
    Call<User> signIn(@Body SignInRequest request);

    @POST("signin/c/social")
    Call<User> signInSocial(@Body SignInRequest request);

    @GET("customer/current")
    Call<User> getCurrentUserInfo();

    @POST("merchant")
    Call<ArrayList<MerchantV2>> getMerchants(@Body MerchantRequest request);

    @GET("merchant/prime")
    Call<Prime> getFavouriteMerchants(@Query("page") int page);

    @POST("merchant/recent")
    Call<ArrayList<Prime.InnerItem>> getMostRecentMerchants(@Query("page") int page);

    @POST("merchant/search/{query}")
    Call<ArrayList<MerchantV2>> searchMerchants(
            @Path("query") String query,
            @Query("page") int page,
            @Query("categories") String categories
    );
}
