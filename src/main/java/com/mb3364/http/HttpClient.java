package com.mb3364.http;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Client for making HTTP requests.
 *
 * @author Matthew Bell
 * @see SyncHttpClient
 * @see AsyncHttpClient
 */
public abstract class HttpClient {

    public static final String DEFAULT_USER_AGENT = "Java-Async-Http";

    private final Map<String, String> headers; // HTTP request headers

    private int connectionTimeout = 20000; // in milliseconds
    private int dataRetrievalTimeout = 20000; // in milliseconds
    private boolean followRedirects = true; // automatically follow HTTP redirects?

    public HttpClient() {
        headers = Collections.synchronizedMap(new LinkedHashMap<String, String>());
        setUserAgent(DEFAULT_USER_AGENT);
    }

    /**
     * Makes an HTTP request.
     *
     * @param url     the URL of the resource to request
     * @param method  the {@link HttpRequestMethod} to use
     * @param params  any parameters to send with the request, or null if none
     * @param handler the response handler
     */
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

    /**
     * Make a HTTP DELETE request.
     *
     * @param url     the URL of the resource to request
     * @param handler the response handler
     * @see #delete(String, RequestParams, HttpResponseHandler)
     */
    public void delete(String url, HttpResponseHandler handler) {
        request(url, HttpRequestMethod.DELETE, null, handler);
    }

    /**
     * Make a HTTP DELETE request with parameters.
     *
     * @param url     the URL of the resource to request
     * @param params  the parameters to send with the request
     * @param handler the response handler
     * @see #delete(String, HttpResponseHandler)
     */
    public void delete(String url, RequestParams params, HttpResponseHandler handler) {
        request(url, HttpRequestMethod.DELETE, params, handler);
    }

    /**
     * Make a HTTP GET request.
     *
     * @param url     the URL of the resource to request
     * @param handler the response handler
     * @see #get(String, RequestParams, HttpResponseHandler)
     */
    public void get(String url, HttpResponseHandler handler) {
        request(url, HttpRequestMethod.GET, null, handler);
    }

    /**
     * Make a HTTP GET request with parameters.
     *
     * @param url     the URL of the resource to request
     * @param params  the parameters to send with the request
     * @param handler the response handler
     * @see #get(String, HttpResponseHandler)
     */
    public void get(String url, RequestParams params, HttpResponseHandler handler) {
        request(url, HttpRequestMethod.GET, params, handler);
    }

    /**
     * Make a HTTP HEAD request.
     *
     * @param url     the URL of the resource to request
     * @param handler the response handler
     * @see #head(String, RequestParams, HttpResponseHandler)
     */
    public void head(String url, HttpResponseHandler handler) {
        request(url, HttpRequestMethod.HEAD, null, handler);
    }

    /**
     * Make a HTTP HEAD request with parameters.
     *
     * @param url     the URL of the resource to request
     * @param params  the parameters to send with the request
     * @param handler the response handler
     * @see #head(String, HttpResponseHandler)
     */
    public void head(String url, RequestParams params, HttpResponseHandler handler) {
        request(url, HttpRequestMethod.HEAD, params, handler);
    }

    /**
     * Make a HTTP POST request.
     *
     * @param url     the URL of the resource to request
     * @param handler the response handler
     * @see #post(String, RequestParams, HttpResponseHandler)
     */
    public void post(String url, HttpResponseHandler handler) {
        request(url, HttpRequestMethod.POST, null, handler);
    }

    /**
     * Make a HTTP POST request with parameters.
     *
     * @param url     the URL of the resource to request
     * @param params  the parameters to send with the request
     * @param handler the response handler
     * @see #post(String, HttpResponseHandler)
     */
    public void post(String url, RequestParams params, HttpResponseHandler handler) {
        request(url, HttpRequestMethod.POST, params, handler);
    }

    /**
     * Make a HTTP PUT request.
     *
     * @param url     the URL of the resource to request
     * @param handler the response handler
     * @see #put(String, RequestParams, HttpResponseHandler)
     */
    public void put(String url, HttpResponseHandler handler) {
        request(url, HttpRequestMethod.PUT, null, handler);
    }

    /**
     * Make a HTTP PUT request with parameters.
     *
     * @param url     the URL of the resource to request
     * @param params  the parameters to send with the request
     * @param handler the response handler
     * @see #put(String, HttpResponseHandler)
     */
    public void put(String url, RequestParams params, HttpResponseHandler handler) {
        request(url, HttpRequestMethod.PUT, params, handler);
    }

    /**
     * Set a global HTTP header that will be sent with all future requests.
     *
     * @param name  the header name
     * @param value the header value
     * @see #removeHeader(String)
     */
    public void setHeader(String name, String value) {
        headers.put(name, value);
    }

    /**
     * Remove a global HTTP header so it is no longer sent with all future requests.
     *
     * @param name the name of the header to remove
     * @see #setHeader(String, String)
     */
    public void removeHeader(String name) {
        headers.remove(name);
    }

    /**
     * Get the User Agent that is sent with requests.
     *
     * @return the User Agent that is sent with requests
     * @see #setUserAgent(String)
     */
    public String getUserAgent() {
        return headers.get("User-Agent");
    }

    /**
     * Set the User Agent that will be sent with all future requests.
     *
     * @param userAgent the User Agent to be set
     * @see #getUserAgent()
     */
    public void setUserAgent(String userAgent) {
        headers.put("User-Agent", userAgent);
    }

    /**
     * Get the currently set Connection Timeout value in milliseconds.
     *
     * @return the connection timeout value in milliseconds
     * @see #setConnectionTimeout(int)
     */
    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    /**
     * Set the Connection Timeout value in milliseconds.
     *
     * @param connectionTimeout the connection timeout value in milliseconds
     * @see #getConnectionTimeout()
     */
    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    /**
     * Get the Data Retrieval Timeout value in milliseconds.
     *
     * @return the Data Retrieval Timeout value in milliseconds
     * @see #setDataRetrievalTimeout(int)
     */
    public int getDataRetrievalTimeout() {
        return dataRetrievalTimeout;
    }

    /**
     * Set the Data Retrieval Timeout value in milliseconds
     *
     * @param dataRetrievalTimeout the Data Retrieval Timeout value in milliseconds
     * @see #getDataRetrievalTimeout()
     */
    public void setDataRetrievalTimeout(int dataRetrievalTimeout) {
        this.dataRetrievalTimeout = dataRetrievalTimeout;
    }

    /**
     * Gets the current value of the Follow Redirects option. If <code>true</code>, all requests
     * will follow 3xx redirect responses.
     *
     * @return <code>true</code> if requests will follow redirect codes automatically, <code>false</code> otherwise
     * @see #setFollowRedirects(boolean)
     */
    public boolean getFollowRedirects() {
        return followRedirects;
    }

    /**
     * Set whether requests should follow 3xx redirects automatically.
     *
     * @param followRedirects <code>true</code> to follow redirects, <code>false</code> otherwise
     * @see #getFollowRedirects()
     */
    public void setFollowRedirects(boolean followRedirects) {
        this.followRedirects = followRedirects;
    }

    /**
     * Set Basic HTTP Authentication credentials that will be sent with all future requests.
     *
     * @param username the authentication username
     * @param password the authentication password
     * @see #clearBasicAuth()
     */
    public void setBasicAuth(String username, String password) {
        /* This Base64 encoder is the only one available in JDK < 8 standard library */
        String encoded = javax.xml.bind.DatatypeConverter.printBase64Binary((username + ":" + password).getBytes());
        headers.put("Authorization", "Basic " + encoded);
    }

    /**
     * Clear the Basic HTTP Authentication credentials that may have been previously set. Credentials will no
     * longer be sent with requests after calling this method.
     *
     * @see #setBasicAuth(String, String)
     */
    public void clearBasicAuth() {
        headers.remove("Authorization");
    }
}
