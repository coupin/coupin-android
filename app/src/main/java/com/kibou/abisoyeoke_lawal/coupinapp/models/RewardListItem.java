package com.kibou.abisoyeoke_lawal.coupinapp.models;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by abisoyeoke-lawal on 7/9/17.
 */

public class RewardListItem implements Serializable {
    public boolean fav = false;
    public boolean later = false;
    public boolean favourited = false;
    public boolean visited = false;
    public Date expiresDate;
    public double latitude;
    public double longitude;
    public int rewardCount= 0;
    public String bookingId;
    public String bookingShortCode;
    public String merchantAddress;
    public String merchantBanner;
    public String merchantLogo;
    public String merchantName;
    public String merchantPhone;
    public String rewardDetails;
    public String rewardDescription;
    public String rewardName;
    public String status;

    public String getBookingId() {
        return bookingId;
    }

    public String getBookingShortCode() {
        return bookingShortCode;
    }

    public Date getExpiresDate() {
        return expiresDate;
    }

    public boolean isLater() {
        return later;
    }

    public boolean isFav() {
        return fav;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public int getRewardCount() {
        return rewardCount;
    }

    public boolean isFavourited() {
        return favourited;
    }

    public String getMerchantAddress() {
        return merchantAddress;
    }

    public String getMerchantBanner() {
        return merchantBanner;
    }

    public String getMerchantLogo() {
        return merchantLogo;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public String getMerchantPhone() {
        return merchantPhone;
    }

    public String getRewardDetails() {
        return rewardDetails;
    }

    public String getStatus(){
        return status;
    }

    public String getRewardDescription() {
        return rewardDescription;
    }

    public String getRewardName() {
        return rewardName;
    }

    public boolean hasVisited() {
        return visited;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public void setBookingShortCode(String bookingShortCode) {
        this.bookingShortCode = bookingShortCode;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setExpiresDate(Date expiresDate) {
        this.expiresDate = expiresDate;
    }

    public void setFav(boolean fav) {
        this.fav = fav;
    }

    public void setFavourited(boolean favourited) {
        this.favourited = favourited;
    }

    public void setLater(boolean later) {
        this.later = later;
    }

    public void setMerchantAddress(String merchantAddress) {
        this.merchantAddress = merchantAddress;
    }

    public void setMerchantBanner(String merchantBanner) {
        this.merchantBanner = merchantBanner;
    }

    public void setMerchantLogo(String merchantLogo) {
        this.merchantLogo = merchantLogo;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public void setMerchantPhone(String merchantPhone) {
        this.merchantPhone = merchantPhone;
    }

    public void setRewardCount(int rewardCount) {
        this.rewardCount = rewardCount;
    }

    public void setRewardDetails(String rewardDetails) {
        this.rewardDetails = rewardDetails;
    }

    public void setRewardDescription(String rewardDescription) {
        this.rewardDescription = rewardDescription;
    }

    public void setRewardName(String rewardName) {
        this.rewardName = rewardName;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public void setStatus(String status){
        this.status = status;
    }
}
