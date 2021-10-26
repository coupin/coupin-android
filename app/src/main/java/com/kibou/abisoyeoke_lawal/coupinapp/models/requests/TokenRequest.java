package com.kibou.abisoyeoke_lawal.coupinapp.models.requests;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TokenRequest implements Serializable {
    @SerializedName("token")
    public String token;

    public TokenRequest(String token) { this.token = token; }
}
