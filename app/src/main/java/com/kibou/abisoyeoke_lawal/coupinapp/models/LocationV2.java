package com.kibou.abisoyeoke_lawal.coupinapp.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class LocationV2 implements Serializable {
    @SerializedName("lat")
    public double latitude;

    @SerializedName("long")
    public double longitude;
}
