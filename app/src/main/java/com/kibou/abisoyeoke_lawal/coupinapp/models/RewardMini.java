package com.kibou.abisoyeoke_lawal.coupinapp.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class RewardMini implements Serializable {
    @SerializedName("pictures")
    public ArrayList<Image> pictures;
    @SerializedName("applicableDays")
    public ArrayList<Integer> days;
    @SerializedName("categories")
    public ArrayList<String> categories;
    @SerializedName("isActive")
    public boolean isActive;
    @SerializedName("delivery")
    public boolean isDelivery;
    @SerializedName("quantity")
    public int quantity;
    @SerializedName("multiple")
    public MultipleV2 multiple;
    @SerializedName("price")
    public PriceV2 price;
    @SerializedName("_id")
    public String id;
    @SerializedName("description")
    public String description;
    @SerializedName("endDate")
    public String endDate;
    @SerializedName("startDate")
    public String startDate;
    @SerializedName("createdDate")
    public String createdDate;
    @SerializedName("modifiedDate")
    public String modifiedDate;
    @SerializedName("name")
    public String name;
    @SerializedName("status")
    public String status;
}
