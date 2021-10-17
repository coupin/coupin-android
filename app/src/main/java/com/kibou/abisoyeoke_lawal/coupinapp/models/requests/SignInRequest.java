package com.kibou.abisoyeoke_lawal.coupinapp.models.requests;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SignInRequest implements Serializable {
    @SerializedName("email")
    public String email;
    @SerializedName("password")
    public String password;

    public SignInRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
