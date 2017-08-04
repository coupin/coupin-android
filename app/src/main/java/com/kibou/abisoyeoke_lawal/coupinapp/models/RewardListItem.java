package com.kibou.abisoyeoke_lawal.coupinapp.models;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by abisoyeoke-lawal on 7/9/17.
 */

public class RewardListItem implements Serializable {
    public Date expiresDate;
    public String bookingId;
    public String bookingShortCode;
    public String merchantAddress;
    public String merchantLogo;
    public String merchantName;
    public String rewardDescription;
    public String rewardName;

    public String getBookingId() {
        return bookingId;
    }

    public String getBookingShortCode() {
        return bookingShortCode;
    }

    public Date getExpiresDate() {
        return expiresDate;
    }

    public String getMerchantAddress() {
        return merchantAddress;
    }

    public String getMerchantLogo() {
        return merchantLogo;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public String getRewardDescription() {
        return rewardDescription;
    }

    public String getRewardName() {
        return rewardName;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public void setBookingShortCode(String bookingShortCode) {
        this.bookingShortCode = bookingShortCode;
    }

    public void setExpiresDate(Date expiresDate) {
        this.expiresDate = expiresDate;
    }

    public void setMerchantAddress(String merchantAddress) {
        this.merchantAddress = merchantAddress;
    }

    public void setMerchantLogo(String merchantLogo) {
        this.merchantLogo = merchantLogo;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public void setRewardDescription(String rewardDescription) {
        this.rewardDescription = rewardDescription;
    }

    public void setRewardName(String rewardName) {
        this.rewardName = rewardName;
    }
}
