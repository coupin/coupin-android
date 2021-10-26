package com.kibou.abisoyeoke_lawal.coupinapp.models.responses;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class GenericResponse implements Serializable {
    @SerializedName("message")
    public String message;
}
