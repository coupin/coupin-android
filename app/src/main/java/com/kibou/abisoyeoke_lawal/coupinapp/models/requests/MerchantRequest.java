package com.kibou.abisoyeoke_lawal.coupinapp.models.requests;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class MerchantRequest implements Serializable {
    @SerializedName("latitude")
    public double latitude;
    @SerializedName("longitude")
    public double longitude;
    @SerializedName("distance")
    public int distance;
    @SerializedName("limit")
    public int limit;
    @SerializedName("page")
    public int page;
    @SerializedName("categories")
    public String categories;

}
