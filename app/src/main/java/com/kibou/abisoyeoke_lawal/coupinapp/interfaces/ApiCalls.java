package com.kibou.abisoyeoke_lawal.coupinapp.interfaces;

import com.kibou.abisoyeoke_lawal.coupinapp.models.Merchant;
import com.kibou.abisoyeoke_lawal.coupinapp.models.MerchantV2;
import com.kibou.abisoyeoke_lawal.coupinapp.models.requests.MerchantRequest;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiCalls {
    @POST("merchant")
    Call<ArrayList<MerchantV2>> getMerchants(@Body MerchantRequest request);
}
