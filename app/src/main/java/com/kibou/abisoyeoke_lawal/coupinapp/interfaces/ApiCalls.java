package com.kibou.abisoyeoke_lawal.coupinapp.interfaces;

import com.kibou.abisoyeoke_lawal.coupinapp.models.Merchant;
import com.kibou.abisoyeoke_lawal.coupinapp.models.MerchantV2;
import com.kibou.abisoyeoke_lawal.coupinapp.models.requests.MerchantRequest;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiCalls {
    @POST("merchant")
    Call<ArrayList<MerchantV2>> getMerchants(@Body MerchantRequest request);

    @POST("merchant/search/{query}")
    Call<ArrayList<MerchantV2>> searchMerchants(
            @Path("query") String query,
            @Query("page") int page,
            @Query("categories") String categories
    );
}
