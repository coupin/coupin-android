package com.kibou.abisoyeoke_lawal.coupinapp.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class MerchantV2 implements Serializable {
    @SerializedName("rewards")
    public ArrayList<Reward> rewards;
    @SerializedName("categories")
    public ArrayList<String> categories;
    @SerializedName("favourite")
    public boolean favourite;
    @SerializedName("visited")
    public boolean visited;
    @SerializedName("banner")
    public Image banner;
    @SerializedName("logo")
    public Image logo;
    @SerializedName("rating")
    public int rating;
    @SerializedName("count")
    public int rewardsCount;
    @SerializedName("location")
    public LocationV2 location;
    @SerializedName("reward")
    public Reward reward;
    @SerializedName("address")
    public String address;
    @SerializedName("category")
    public String category;
    @SerializedName("details")
    public String details;
    @SerializedName("email")
    public String email;
    @SerializedName("_id")
    public String id;
    @SerializedName("mobile")
    public String mobile;
    @SerializedName("name")
    public String name;

    // Unsure for now
    public int picture;
    public String title;
    public String response;
}
