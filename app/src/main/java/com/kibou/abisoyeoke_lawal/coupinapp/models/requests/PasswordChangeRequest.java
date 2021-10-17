package com.kibou.abisoyeoke_lawal.coupinapp.models.requests;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PasswordChangeRequest implements Serializable {
    @SerializedName("email")
    public String email;
    @SerializedName("password")
    public String password;

    public PasswordChangeRequest() {}

    public PasswordChangeRequest(String password) {
        this.password = password;
    }
}