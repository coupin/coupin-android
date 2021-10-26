package com.kibou.abisoyeoke_lawal.coupinapp.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class RewardV2 implements Serializable {
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

    public boolean isDiscount;
    public boolean isMultiple;
    public boolean isSelected = false;
    public Date expires;
    public Date starting;
    public int selectedQuantity = 1;
    public String title;

    public RewardV2() {
        this.isMultiple = this.multiple != null && this.multiple.status;
    }
}
