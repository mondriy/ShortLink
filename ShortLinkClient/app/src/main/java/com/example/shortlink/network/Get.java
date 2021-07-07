package com.example.shortlink.network;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;

public class Get {
    @SerializedName("originalUrl")
    @Expose
    private String originalUrl;
    @SerializedName("shortLink")
    @Expose
    private String shortLink;
    @SerializedName("expirationDate")
    @Expose
    private String expirationDate;

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public String getShortLink() {
        return shortLink;
    }

    public void setShortLink(String shortLink) {
        this.shortLink = shortLink;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void String(String expirationDate) {
        this.expirationDate = expirationDate;
    }
}
