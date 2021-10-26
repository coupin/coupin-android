package com.kibou.abisoyeoke_lawal.coupinapp.models;

import com.google.gson.annotations.SerializedName;
import com.kibou.abisoyeoke_lawal.coupinapp.models.requests.InnerItemMini;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by abisoyeoke-lawal on 7/9/17.
 */

public class RewardListItem implements Serializable {
    @SerializedName("rewardId")
    public ArrayList<RewardWrapperMini> rewards;
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
    public InnerItemMini merchant;
    @SerializedName("userid")
    public User userid;

    public boolean later = false;
    public Date expiresDate;
    public double latitude;
    public double longitude;
    public int rewardCount= 0;
    public String bookingId;
    public String merchantName;

    public static class RewardWrapperMini implements Serializable {
        @SerializedName("id")
        public RewardMini reward;
    }
}
