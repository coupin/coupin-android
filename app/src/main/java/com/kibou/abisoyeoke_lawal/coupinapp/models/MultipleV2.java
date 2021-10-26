package com.kibou.abisoyeoke_lawal.coupinapp.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class MultipleV2 implements Serializable {
    @SerializedName("status")
    public boolean status;
    @SerializedName("capacity")
    public int capacity;
}
