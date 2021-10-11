package com.kibou.abisoyeoke_lawal.coupinapp.interfaces;

import com.kibou.abisoyeoke_lawal.coupinapp.models.BookingResponse;
import com.kibou.abisoyeoke_lawal.coupinapp.models.Favourite;
import com.kibou.abisoyeoke_lawal.coupinapp.models.GenericResponse;
import com.kibou.abisoyeoke_lawal.coupinapp.models.InnerItem;
import com.kibou.abisoyeoke_lawal.coupinapp.models.MerchantV2;
import com.kibou.abisoyeoke_lawal.coupinapp.models.Prime;
import com.kibou.abisoyeoke_lawal.coupinapp.models.RewardListItem;
import com.kibou.abisoyeoke_lawal.coupinapp.models.RewardV2;
import com.kibou.abisoyeoke_lawal.coupinapp.models.RewardsListItemV2;
import com.kibou.abisoyeoke_lawal.coupinapp.models.User;
import com.kibou.abisoyeoke_lawal.coupinapp.models.requests.CoupinRequest;
import com.kibou.abisoyeoke_lawal.coupinapp.models.requests.FeedbackRequest;
import com.kibou.abisoyeoke_lawal.coupinapp.models.requests.InterestsRequest;
import com.kibou.abisoyeoke_lawal.coupinapp.models.requests.MerchantRequest;
import com.kibou.abisoyeoke_lawal.coupinapp.models.requests.PasswordChangeRequest;
import com.kibou.abisoyeoke_lawal.coupinapp.models.requests.SignInRequest;
import com.kibou.abisoyeoke_lawal.coupinapp.models.requests.TokenRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiCalls {
    @POST("auth/forgot-password")
    Call<GenericResponse> requestPasswordChange(@Body PasswordChangeRequest request);

    @POST("auth/signin/c")
    Call<User> signIn(@Body SignInRequest request);

    @POST("auth/signin/c/social")
    Call<User> signInSocial(@Body SignInRequest request);

    @GET("coupin")
    Call<ArrayList<RewardsListItemV2>> getCoupins(@Query("saved") boolean saved, @Query("page") int page);

    @POST("coupin")
    Call<BookingResponse> createCoupin(@Body CoupinRequest body);

    @PUT("coupin/{id}/activate")
    Call<BookingResponse> activateCoupin(@Path("id") String merchantId);

    @PUT("customer/category")
    Call<User> updateInterestInfo(@Body InterestsRequest body);

    @POST("customer/category")
    Call<User> saveInterestInfo(@Body InterestsRequest body);

    @POST("customer/{customerId}")
    Call<User> saveCurrentUserInfo(@Body User user);

    @GET("customer/current")
    Call<User> getCurrentUserInfo();

    @GET("customer/favourites")
    Call<ArrayList<Favourite>> getFavourites();

    @POST("customer/favourites")
    Call<User> addToFavourites(@Body HashMap<String, String> body);

    @PUT("customer/favourites")
    Call<User> removeFromFavourites(@Body HashMap<String, String> body);

    @POST("customer/feedback")
    Call<GenericResponse> sendFeedback(@Body FeedbackRequest request);

    @POST("customer/notifications/{userId}")
    Call<GenericResponse> setNotification(@Body TokenRequest request);

    @POST("merchant")
    Call<ArrayList<MerchantV2>> getMerchants(@Body MerchantRequest request);

    @GET("merchant/prime")
    Call<Prime> getFavouriteMerchants(@Query("page") int page);

    @POST("merchant/recent")
    Call<ArrayList<InnerItem>> getMostRecentMerchants(@Query("page") int page);

    @POST("merchant/search/{query}")
    Call<ArrayList<MerchantV2>> searchMerchants(
            @Path("query") String query,
            @Query("page") int page,
            @Query("categories") String categories
    );

    @GET("rewards/merchant/{merchantId}")
    Call<ArrayList<RewardV2>> getMerchantRewards(@Path("merchantId") String id, @Query("page") int page);
}
