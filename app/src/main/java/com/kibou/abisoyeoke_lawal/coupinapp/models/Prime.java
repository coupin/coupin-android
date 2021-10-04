package com.kibou.abisoyeoke_lawal.coupinapp.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class Prime implements Serializable {
    @SerializedName("hotlist")
    public ArrayList<HotList> hotList;
    @SerializedName("featured")
    public Featured featured;
    @SerializedName("_id")
    public String id;
    @SerializedName("visited")
    public Visited visited;


    public static class Featured implements Serializable {
        @SerializedName("first")
        public InnerItem first;
        @SerializedName("second")
        public InnerItem second;
        @SerializedName("third")
        public InnerItem third;
        @SerializedName("_id")
        public String id;
        @SerializedName("url")
        public String url;
        @SerializedName("id")
        public InnerItem merchant;
    }

    public static class HotList implements Serializable {
        @SerializedName("index")
        public int index;
        @SerializedName("_id")
        public String id;
        @SerializedName("url")
        public String url;
        @SerializedName("id")
        public InnerItem merchant;
    }

    public static class Visited implements Serializable {
        @SerializedName("first")
        public boolean first;
        @SerializedName("second")
        public boolean second;
        @SerializedName("third")
        public boolean third;
    }
}
