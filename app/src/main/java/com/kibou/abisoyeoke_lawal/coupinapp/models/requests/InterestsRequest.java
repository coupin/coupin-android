package com.kibou.abisoyeoke_lawal.coupinapp.models.requests;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class InterestsRequest implements Serializable {
    @SerializedName("interests")
    public String interests;

    public InterestsRequest(String interests) { this.interests = interests; }
}
