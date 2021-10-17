package com.kibou.abisoyeoke_lawal.coupinapp.models.requests;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class FeedbackRequest implements Serializable {
    @SerializedName("coupinCode")
    public String coupinCode;
    @SerializedName("merchantName")
    public String merchantName;
    @SerializedName("message")
    public String message;
}
