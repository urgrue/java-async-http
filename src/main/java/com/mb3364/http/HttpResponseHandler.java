package com.mb3364.http;

public abstract class HttpResponseHandler {
    public abstract void onSuccess(HttpResponse response);

    public abstract void onFailure(HttpResponse response);

    public abstract void onFailure(Throwable throwable);
}
