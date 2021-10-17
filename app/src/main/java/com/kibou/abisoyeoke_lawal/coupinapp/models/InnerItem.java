package com.kibou.abisoyeoke_lawal.coupinapp.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class InnerItem implements Serializable {
    @SerializedName("favourite")
    public boolean favourite;
    @SerializedName("visited")
    public boolean visited;
    @SerializedName("merchantInfo")
    public MerchantInfo merchantInfo;
    @SerializedName("email")
    public String email;
    @SerializedName("_id")
    public String id;

    public static class MerchantInfo implements Serializable {
        @SerializedName("rewards")
        public ArrayList<RewardV2> rewards;
        @SerializedName("categories")
        public ArrayList<String> categories;
        @SerializedName("location")
        public double[] location;
        @SerializedName("banner")
        public Image banner;
        @SerializedName("logo")
        public Image logo;
        @SerializedName("rating")
        public int rating;
        @SerializedName("address")
        public String address;
        @SerializedName("city")
        public String city;
        @SerializedName("companyDetails")
        public String companyDetails;
        @SerializedName("companyName")
        public String companyName;
        @SerializedName("mobileNumber")
        public String mobileNumber;
    }
}
