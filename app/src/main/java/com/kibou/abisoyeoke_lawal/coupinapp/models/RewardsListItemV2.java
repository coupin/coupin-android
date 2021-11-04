package com.kibou.abisoyeoke_lawal.coupinapp.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class RewardsListItemV2 implements Serializable {
    @SerializedName("rewardId")
    public ArrayList<RewardWrapper> rewards;
    @SerializedName("rewardsArray")
    public ArrayList<Reward> rewardsArray;
    @SerializedName("favourite")
    public boolean favourite;
    @SerializedName("isActive")
    public boolean isActive;
    @SerializedName("visited")
    public boolean visited;
    @SerializedName("useNow")
    public boolean useNow;
    @SerializedName("createdAt")
    public String createdAt;
    @SerializedName("expiryDate")
    public String expiryDate;
    @SerializedName("_id")
    public String id;
    @SerializedName("shortCode")
    public String shortCode;
    @SerializedName("status")
    public String status;
    @SerializedName("merchantId")
    public InnerItem merchant;
    @SerializedName("userid")
    public User userid;

    public static class RewardWrapper implements Serializable {
        @SerializedName("singleUse")
        public boolean singleUse;
        @SerializedName("quantity")
        public int quantity;
        @SerializedName("id")
        public Reward reward;
        @SerializedName("status")
        public String status;
        @SerializedName("usedOn")
        public String usedOn;
    }

    public boolean later;
    public Date expiresDate;
    public int rewardCount;
    public String bookingId;
}
