package com.kibou.abisoyeoke_lawal.coupinapp.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class BookingResponse implements Serializable {
    @SerializedName("data")
    public BookingData data;

    public class BookingData implements Serializable {
        @SerializedName("rewardId")
        public BookingViewModel booking;
        @SerializedName("reference")
        public String reference;
    }

    public static class BookingDelivery implements Serializable {
        @SerializedName("id")
        public String id;
        @SerializedName("status")
        public String status;
    }

    public static class BookingReward implements Serializable {
        @SerializedName("singleUse")
        public boolean singleUse;
        @SerializedName("quantity")
        public int quantity;
        @SerializedName("id")
        public String id;
        @SerializedName("status")
        public String status;
        @SerializedName("usedOn")
        public String usedOn;
    }

    public static class BookingTransactions implements Serializable {
        @SerializedName("reference")
        public String reference;
        @SerializedName("paymentReference")
        public String paymentReference;
        @SerializedName("status")
        public String status;
    }

    public class BookingViewModel {
        @SerializedName("rewardId")
        public ArrayList<BookingReward> rewardId;
        @SerializedName("transactions")
        public ArrayList<BookingTransactions> transactions;
        @SerializedName("isActive")
        public boolean isActive;
        @SerializedName("isDeliverable")
        public boolean isDeliverable;
        @SerializedName("useNow")
        public boolean useNow;
        @SerializedName("delivery")
        public BookingDelivery delivery;
        @SerializedName("createdAt")
        public String createdAt;
        @SerializedName("deliveryAddress")
        public String deliveryAddress;
        @SerializedName("merchantId")
        public String merchantId;
        @SerializedName("expiryDate")
        public String expiryDate;
        @SerializedName("shortCode")
        public String shortCode;
        @SerializedName("status")
        public String status;
        @SerializedName("userId")
        public String userId;
    }
}
