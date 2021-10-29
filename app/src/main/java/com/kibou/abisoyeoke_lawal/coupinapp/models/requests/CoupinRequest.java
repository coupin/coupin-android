package com.kibou.abisoyeoke_lawal.coupinapp.models.requests;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CoupinRequest implements Serializable {
    @SerializedName("useNow")
    public boolean useNow;
    @SerializedName("saved")
    public boolean saved;
    @SerializedName("expiryDate")
    public String expiryDate;
    @SerializedName("merchantId")
    public String merchantId;
    @SerializedName("rewardId")
    public String rewardId;

    public CoupinRequest(boolean useNow, String expiryDate, String merchantId, String rewardId) {
        this.expiryDate = expiryDate;
        this.merchantId = merchantId;
        this.rewardId = rewardId;
        this.saved = !useNow;
        this.useNow = useNow;
    }
}
