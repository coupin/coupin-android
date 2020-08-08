package com.kibou.abisoyeoke_lawal.coupinapp.models;

import org.json.JSONArray;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by abisoyeoke-lawal on 8/13/17.
 */

public class Reward implements Serializable {
  private boolean isDelivery;
  private boolean isDiscount;
  private boolean isMultiple;
  private boolean isSelected = false;
  private Date expires;
  private Date starting;
  private float newPrice;
  private float oldPrice;
  private JSONArray days;
  private JSONArray pictures;
  private String details;
  private String id;
  private String title;

    public void setDays(JSONArray days) {
        this.days = days;
    }

    public void setDetails(String details) {
      this.details = details;
  }

    public void setExpires(Date expires) {
        this.expires = expires;
    }

    public void setId(String id) {
    this.id = id;
  }

    public void setIsDelivery(boolean delivery) {
        isDelivery = delivery;
    }
    public void setIsDiscount(boolean discount) {
        isDiscount = discount;
    }

    public void setMultiple(boolean multiple) {
        isMultiple = multiple;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public void setNewPrice(int newPrice) {
      this.newPrice = newPrice;
  }

    public void setOldPrice(int oldPrice) {
      this.oldPrice = oldPrice;
  }

    public void setPictures(JSONArray pictures) {
        this.pictures = pictures;
    }

    public void setStarting(Date starting) {
        this.starting = starting;
    }

    public void setTitle(String title) {
      this.title = title;
  }

    public JSONArray getDays() {
        return days;
    }

    public String getDetails() {
      return details;
  }

  public Date getExpires() {
        return expires;
    }

  public String getId() {
    return id;
  }

  public boolean getIsDelivery() {
        return isDelivery;
    }

  public boolean getIsDiscount() {
        return isDiscount;
    }

    public boolean isSelected() {
        return isSelected;
    }

  public boolean getMultiple() {
      return isMultiple;
  }

  public float getNewPrice() {
      return newPrice;
  }

  public float getOldPrice() {
      return oldPrice;
  }

  public JSONArray getPictures() {
      return pictures;
  }

    public Date getStarting() {
        return starting;
    }

    public String getTitle() {
        return title;
    }
}
