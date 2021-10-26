package com.kibou.abisoyeoke_lawal.coupinapp.models.requests;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SignUpRequest implements Serializable {
    @SerializedName("email")
    public String email;
    @SerializedName("facebookId")
    public String facebookId;
    @SerializedName("googleId")
    public String googleId;
    @SerializedName("password")
    public String password;
    @SerializedName("password2")
    public String password2;
    @SerializedName("pictureUrl")
    public String pictureUrl;
    @SerializedName("name")
    public String name;

    public SignUpRequest() {};

    public SignUpRequest(String name, String email, String id, boolean isGoogle, @Nullable String pictureUrl) {
        this.name = name;
        this.email = email;
        if (isGoogle) {
            googleId = id;
        } else {
            facebookId = id;
        }
        this.pictureUrl = pictureUrl;
    };

    public SignUpRequest(String name, String email, String password, String password2) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.password2 = password2;
    };
}
