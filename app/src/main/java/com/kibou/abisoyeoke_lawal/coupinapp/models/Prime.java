package com.kibou.abisoyeoke_lawal.coupinapp.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class Prime implements Serializable {
    @SerializedName("hotlist")
    public ArrayList<HotList> hotList;
    @SerializedName("featured")
    public Featured featured;
    @SerializedName("_id")
    public String id;
    @SerializedName("visited")
    public Visited visited;


    public static class Featured implements Serializable {
        @SerializedName("first")
        public InnerItem first;
        @SerializedName("second")
        public InnerItem second;
        @SerializedName("third")
        public InnerItem third;
        @SerializedName("_id")
        public String id;
        @SerializedName("url")
        public String url;
        @SerializedName("id")
        public InnerItem merchant;
    }

    public static class HotList implements Serializable {
        @SerializedName("index")
        public int index;
        @SerializedName("_id")
        public String id;
        @SerializedName("url")
        public String url;
        @SerializedName("id")
        public InnerItem merchant;
    }

    public static class Visited implements Serializable {
        @SerializedName("first")
        public boolean first;
        @SerializedName("second")
        public boolean second;
        @SerializedName("third")
        public boolean third;
    }

    public static class InnerItem implements Serializable {
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
    }

    public static class MerchantInfo implements Serializable {
        @SerializedName("rewards")
        public ArrayList<RewardV2> rewards;
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
