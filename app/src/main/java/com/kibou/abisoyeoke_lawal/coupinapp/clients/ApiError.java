package com.kibou.abisoyeoke_lawal.coupinapp.clients;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ApiError implements Serializable {
    @SerializedName("statusCode")
    public int statusCode;
    @SerializedName("message")
    public String message;

    public ApiError() {
        this.message = "An error occurred on the server side. Please try again later or contact support.";
    }

    public ApiError(String message) {
        this.message = message;
    }

    public ApiError(String message, int statusCode) {
        this.message = message;
        this.statusCode = statusCode;
    }
}
