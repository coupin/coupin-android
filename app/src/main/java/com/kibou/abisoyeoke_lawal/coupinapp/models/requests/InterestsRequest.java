package com.kibou.abisoyeoke_lawal.coupinapp.models.requests;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class InterestsRequest implements Serializable {
    @SerializedName("interests")
    public ArrayList<String> interests;

    public InterestsRequest(ArrayList<String> interests) { this.interests = interests; }
}
