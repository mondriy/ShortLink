package com.example.shortlink.network;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Post {
    @SerializedName("url")
    @Expose
    private String url;

    public Post(String url) {
        this.url = url;
    }

    public Post() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
