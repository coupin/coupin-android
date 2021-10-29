package com.kibou.abisoyeoke_lawal.coupinapp.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SelectedReward implements Serializable {
    @SerializedName("quantity")
    public int quantity;
    @SerializedName("rewardId")
    public String rewardId;

    public SelectedReward(String rewardId, int quantity) {
        this.quantity = quantity;
        this.rewardId = rewardId;
    }
}
