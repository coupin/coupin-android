package com.kibou.abisoyeoke_lawal.coupinapp.models;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by abisoyeoke-lawal on 8/13/17.
 */

public class Reward implements Serializable {
    private boolean isDiscount;
    private Date expires;
    private float newPrice;
    private float oldPrice;
    private String details;
    private String title;

    public void setDetails(String details) {
        this.details = details;
    }

    public void setExpires(Date expires) {
        this.expires = expires;
    }

    public void setIsDiscount(boolean discount) {
        isDiscount = discount;
    }

    public void setNewPrice(int newPrice) {
        this.newPrice = newPrice;
    }

    public void setOldPrice(int oldPrice) {
        this.oldPrice = oldPrice;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetails() {
        return details;
    }

    public Date getExpires() {
        return expires;
    }

    public boolean getIsDiscount() {
        return isDiscount;
    }

    public float getNewPrice() {
        return newPrice;
    }

    public float getOldPrice() {
        return oldPrice;
    }

    public String getTitle() {
        return title;
    }
}
