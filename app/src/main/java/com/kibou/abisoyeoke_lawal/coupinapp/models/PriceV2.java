package com.kibou.abisoyeoke_lawal.coupinapp.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PriceV2 implements Serializable {
    @SerializedName("new")
    public float newPrice;
    @SerializedName("old")
    public float oldPrice;
}
