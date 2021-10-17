package com.kibou.abisoyeoke_lawal.coupinapp.models.responses;

import com.google.gson.annotations.SerializedName;
import com.kibou.abisoyeoke_lawal.coupinapp.models.User;

import java.io.Serializable;

public class AuthResponse implements Serializable {
    @SerializedName("message")
    public String message;
    @SerializedName("token")
    public String token;
    @SerializedName("user")
    public User user;
}
