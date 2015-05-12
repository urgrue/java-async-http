package com.mb3364.http;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class HttpClient {

    public static final String DEFAULT_USER_AGENT = "Java-Async-Http";

    private final Map<String, String> headers;

    private int connectionTimeout = 20000;
    private int dataRetrievalTimeout = 20000;
    private boolean followRedirects = true;

    public HttpClient() {
        headers = Collections.synchronizedMap(new LinkedHashMap<String, String>());
        setUserAgent(DEFAULT_USER_AGENT);
    }

    protected void request(String url, HttpRequestMethod method, RequestParams params, HttpResponseHandler handler) {

        HttpURLConnection urlConnection = null;

        // Create empty params if one isn't specified
        if (params == null) {
            params = new RequestParams();
        }

        // Append params to url for methods other than POST and PUT
        if (method != HttpRequestMethod.POST && method != HttpRequestMethod.PUT) {
            if (params.size() > 0) url = url + "?" + params.toEncodedString();
        }

        try {
            URL resourceUrl = new URL(url);
            urlConnection = (HttpURLConnection) resourceUrl.openConnection();

            // Settings
            urlConnection.setConnectTimeout(connectionTimeout);
            urlConnection.setReadTimeout(dataRetrievalTimeout);
            urlConnection.setUseCaches(false);
            urlConnection.setInstanceFollowRedirects(followRedirects);
            urlConnection.setRequestMethod(method.toString());
            urlConnection.setDoInput(true);

            // Headers
            for (Map.Entry<String, String> header : headers.entrySet()) {
                urlConnection.setRequestProperty(header.getKey(), header.getValue());
            }

            handler.onStart(urlConnection);

            // Request Body
            // POST and PUT expect an output body.
            if (method == HttpRequestMethod.POST || method == HttpRequestMethod.PUT) {
                urlConnection.setDoOutput(true);
                if (params.hasFiles()) {
                    // Use multipart/form-data to send fields and files
                    urlConnection.setChunkedStreamingMode(32 * 1024); // 32kb at a time
                    MultipartWriter.write(urlConnection, params);
                } else {
                    // Send content as form-urlencoded
                    byte[] content = params.toEncodedString().getBytes();
                    urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + params.getCharset().name());
                    urlConnection.setRequestProperty("Content-Length", Long.toString(content.length));
                    urlConnection.setFixedLengthStreamingMode(content.length); // Stream the data so we don't run out of memory
                    try (OutputStream os = urlConnection.getOutputStream()) {
                        os.write(content);
                    }
                }
            }

            // Process the response in the handler because it can be done in different ways
            handler.processResponse(urlConnection);
            // Request finished
            handler.onFinish(urlConnection);

        } catch (IOException e) {
            handler.onFailure(e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    public void delete(String url, HttpResponseHandler handler) {
        request(url, HttpRequestMethod.DELETE, null, handler);
    }

    public void delete(String url, RequestParams params, HttpResponseHandler handler) {
        request(url, HttpRequestMethod.DELETE, params, handler);
    }

    public void get(String url, HttpResponseHandler handler) {
        request(url, HttpRequestMethod.GET, null, handler);
    }

    public void get(String url, RequestParams params, HttpResponseHandler handler) {
        request(url, HttpRequestMethod.GET, params, handler);
    }

    public void head(String url, HttpResponseHandler handler) {
        request(url, HttpRequestMethod.HEAD, null, handler);
    }

    public void head(String url, RequestParams params, HttpResponseHandler handler) {
        request(url, HttpRequestMethod.HEAD, params, handler);
    }

    public void post(String url, HttpResponseHandler handler) {
        request(url, HttpRequestMethod.POST, null, handler);
    }

    public void post(String url, RequestParams params, HttpResponseHandler handler) {
        request(url, HttpRequestMethod.POST, params, handler);
    }

    public void put(String url, HttpResponseHandler handler) {
        request(url, HttpRequestMethod.PUT, null, handler);
    }

    public void put(String url, RequestParams params, HttpResponseHandler handler) {
        request(url, HttpRequestMethod.PUT, params, handler);
    }

    public void setHeader(String name, String value) {
        headers.put(name, value);
    }

    public void removeHeader(String name) {
        headers.remove(name);
    }

    public String getUserAgent() {
        return headers.get("User-Agent");
    }

    public void setUserAgent(String userAgent) {
        headers.put("User-Agent", userAgent);
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public int getDataRetrievalTimeout() {
        return dataRetrievalTimeout;
    }

    public void setDataRetrievalTimeout(int dataRetrievalTimeout) {
        this.dataRetrievalTimeout = dataRetrievalTimeout;
    }

    public boolean getFollowRedirects() {
        return followRedirects;
    }

    public void setFollowRedirects(boolean followRedirects) {
        this.followRedirects = followRedirects;
    }

    public void setBasicAuth(String username, String password) {
        /* This Base64 encoder is the only one available in JDK < 8 standard library */
        String encoded = javax.xml.bind.DatatypeConverter.printBase64Binary((username + ":" + password).getBytes());
        headers.put("Authorization", "Basic " + encoded);
    }

    public void clearBasicAuth() {
        headers.remove("Authorization");
    }
}
