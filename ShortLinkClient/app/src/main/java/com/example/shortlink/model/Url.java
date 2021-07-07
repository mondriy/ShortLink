package com.example.shortlink.model;

public class Url {
    private static String url;
    private static String originalUrl;

    public static String getUrl() {
        return url;
    }

    public static void setUrl(String url) {
        Url.url = url;
    }

    public static String getOriginalUrl() {
        return originalUrl;
    }

    public static void setOriginalUrl(String originalUrl) {
        Url.originalUrl = originalUrl;
    }
}
