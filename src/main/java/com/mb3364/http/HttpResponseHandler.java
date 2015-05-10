package com.mb3364.http;

import java.util.List;
import java.util.Map;

public abstract class HttpResponseHandler {

    public abstract void onSuccess(int statusCode, Map<String, List<String>> headers, byte[] content);

    public abstract void onFailure(int statusCode, Map<String, List<String>> headers, byte[] content);

    public abstract void onFailure(Throwable throwable);
}
