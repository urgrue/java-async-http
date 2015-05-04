package com.mb3364.http;

/**
 * HTTP Request Methods.
 * <p>
 * These are the valid request methods accepted by {@link java.net.HttpURLConnection} as
 * specified in {@link java.net.HttpURLConnection#setRequestMethod(String)}.
 * </p>
 */
public enum HttpRequestMethod {
    DELETE("DELETE"),
    GET("GET"),
    HEAD("HEAD"),
    OPTIONS("OPTIONS"),
    POST("POST"),
    PUT("PUT"),
    TRACE("TRACE");

    String key;

    HttpRequestMethod(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return key;
    }
}
