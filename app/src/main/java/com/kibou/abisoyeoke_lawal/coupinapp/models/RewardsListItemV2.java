package com.kibou.abisoyeoke_lawal.coupinapp.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class RewardsListItemV2 implements Serializable {
    @SerializedName("rewardId")
    public ArrayList<RewardWrapper> rewards;
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
        @SerializedName("id")
        public RewardV2 reward;
    }

    public boolean later;
    public Date expiresDate;
    public int rewardCount;
    public String bookingId;
//    public String bookingShortCode;
//    public String merchantAddress;
//    public String merchantBanner;
//    public String merchantLogo;
//    public String merchantName;
//    public String merchantPhone;
//    public String rewardDetails;
//    public String rewardDescription;
//    public String rewardName;
}
