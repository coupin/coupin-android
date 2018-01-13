package com.kibou.abisoyeoke_lawal.coupinapp.models;

import java.io.Serializable;

/**
 * Created by abisoyeoke-lawal on 6/3/17.
 */

public class Merchant implements Serializable {
    public String id;
    public int picture;
    public String title;
    public String email;
    public String mobile;
    public String details;
    public String address;
    public String response;
    public String rewards;
    public double latitude;
    public double longitude;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getPicture() {
        return picture;
    }

    public void setPicture(int picture) {
        this.picture = picture;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getRewards() {
        return rewards;
    }

    public void setRewards(String rewards) {
        this.rewards = rewards;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
