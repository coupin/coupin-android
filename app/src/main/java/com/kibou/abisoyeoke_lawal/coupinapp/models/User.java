package com.kibou.abisoyeoke_lawal.coupinapp.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {
    @SerializedName("appliedDiscounts")
    public ArrayList<String> appliedDiscounts;
    @SerializedName("blacklist")
    public ArrayList<String> blacklist;
    @SerializedName("favourites")
    public ArrayList<String> favourites;
    @SerializedName("interests")
    public ArrayList<String> interests;
    @SerializedName("visited")
    public ArrayList<String> visited;
    @SerializedName("isActive")
    public boolean isActive;
    @SerializedName("picture")
    public Image picture;
    @SerializedName("notification")
    public Notification notification;
    @SerializedName("_id")
    public String id;
    @SerializedName("email")
    public String email;
    @SerializedName("name")
    public String name;
    @SerializedName("dateOfBirth")
    public String dateOfBirth;
    @SerializedName("mobileNumber")
    public String mobileNumber;
    @SerializedName("ageRange")
    public String ageRange;
    @SerializedName("sex")
    public String sex;
    @SerializedName("city")
    public String city;
    @SerializedName("createdDate")
    public String createdDate;
    @SerializedName("modifiedDate")
    public String modifiedDate;
    @SerializedName("notify")
    public String notify;
    @SerializedName("days")
    public String days;
    @SerializedName("referralCode")
    public String referralCode;

    public static class Notification implements Serializable {
        @SerializedName("notify")
        public boolean notify;
        @SerializedName("days")
        public String days;
        @SerializedName("token")
        public String token;
    }
}
