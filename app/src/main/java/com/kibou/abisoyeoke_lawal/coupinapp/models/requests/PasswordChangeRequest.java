package com.kibou.abisoyeoke_lawal.coupinapp.models.requests;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PasswordChangeRequest implements Serializable {
    @SerializedName("email")
    public String email;
}
