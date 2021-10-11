package com.kibou.abisoyeoke_lawal.coupinapp.models.requests;

import com.google.gson.annotations.SerializedName;
import com.kibou.abisoyeoke_lawal.coupinapp.models.Image;
import com.kibou.abisoyeoke_lawal.coupinapp.models.InnerItem;
import com.kibou.abisoyeoke_lawal.coupinapp.models.Rating;
import com.kibou.abisoyeoke_lawal.coupinapp.models.RewardV2;

import java.io.Serializable;
import java.util.ArrayList;

public class InnerItemMini {
    @SerializedName("favourite")
    public boolean favourite;
    @SerializedName("visited")
    public boolean visited;
    @SerializedName("merchantInfo")
    public MerchantInfoMini merchantInfo;
    @SerializedName("email")
    public String email;
    @SerializedName("_id")
    public String id;

    public static class MerchantInfoMini implements Serializable {
        @SerializedName("rewards")
        public ArrayList<String> rewards;
        @SerializedName("categories")
        public ArrayList<String> categories;
        @SerializedName("banner")
        public Image banner;
        @SerializedName("logo")
        public Image logo;
        @SerializedName("location")
        public double[] location;
        @SerializedName("rating")
        public Rating rating;
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
