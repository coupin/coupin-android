package com.kibou.abisoyeoke_lawal.coupinapp.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class Favourite implements Serializable {
    @SerializedName("rewards")
    public ArrayList<RewardMini> rewards;
    @SerializedName("favourite")
    public boolean favourite;
    @SerializedName("visited")
    public boolean visited;
    @SerializedName("banner")
    public Image banner;
    @SerializedName("logo")
    public Image logo;
    @SerializedName("picture")
    public int picture;
    @SerializedName("rating")
    public int rating;
    @SerializedName("location")
    public LocationV2 location;
    @SerializedName("reward")
    public RewardMini reward;
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
    @SerializedName("status")
    public String status;
}
