package com.mb3364.http;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class HttpClient {

    public static final String DEFAULT_USER_AGENT = "Java-Async-Http";

    private final Map<String, String> headers;

    private int connectionTimeout = 20000;
    private int dataRetrievalTimeout = 20000;

    public HttpClient() {
        headers = Collections.synchronizedMap(new LinkedHashMap<String, String>());
        setUserAgent(DEFAULT_USER_AGENT);
    }

    /**
     * Reads an InputStream into a byte array.
     *
     * @param inputStream InputStream to read
     * @return byte array representing entire InputStream contents
     * @throws IOException if unable to read stream
     */
    private static byte[] readStreamAsBytes(InputStream inputStream) throws IOException {
        if (inputStream == null) return new byte[0];

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024 * 32];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer, 0, buffer.length)) != -1) {
            os.write(buffer, 0, bytesRead);
        }
        os.flush();
        return os.toByteArray();
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
            urlConnection.setRequestMethod(method.toString());
            urlConnection.setDoInput(true);

            // Headers
            for (Map.Entry<String, String> header : headers.entrySet()) {
                urlConnection.setRequestProperty(header.getKey(), header.getValue());
            }

            // Request Body
            // POST and PUT expect an output body.
            if (method == HttpRequestMethod.POST || method == HttpRequestMethod.PUT) {
                urlConnection.setDoOutput(true);
                byte[] content = params.toEncodedString().getBytes();
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + "utf-8"); // TODO: Allow user to set content-type
                urlConnection.setRequestProperty("Content-Length", Long.toString(content.length));
                urlConnection.setFixedLengthStreamingMode(content.length); // Stream the data so we don't run out of memory
                try (OutputStream os = urlConnection.getOutputStream()) {
                    os.write(content);
                }
            }

            // Response
            int responseCode = urlConnection.getResponseCode();
            Map<String, List<String>> responseHeaders = urlConnection.getHeaderFields();

            // 'Successful' response codes will be in interval [200,300)
            if (responseCode >= 200 && responseCode < 300) {
                byte[] responseContent = readStreamAsBytes(urlConnection.getInputStream());
                handler.onSuccess(responseCode, responseHeaders, responseContent);
            } else {
                byte[] responseContent = readStreamAsBytes(urlConnection.getErrorStream());
                handler.onFailure(responseCode, responseHeaders, responseContent);
            }

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
}
