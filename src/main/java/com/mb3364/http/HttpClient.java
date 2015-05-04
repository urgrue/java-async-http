package com.mb3364.http;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HttpClient {

    public static final String DEFAULT_USER_AGENT = "Java-Async-Http";
    public static final String DEFAULT_CHARSET = "UTF-8";

    private final Map<String, String> headers;

    private String charset;
    private int connectionTimeout = 20000;
    private int dataRetrievalTimeout = 20000;

    public HttpClient() {
        headers = Collections.synchronizedMap(new LinkedHashMap<>());
        setUserAgent(DEFAULT_USER_AGENT);
        setCharset(DEFAULT_CHARSET);
    }

    /**
     * Read the input stream and convert to a string
     *
     * @param inputStream InputStream to read
     * @return String representing entire input stream contents
     */
    private static String readStream(InputStream inputStream) {
        if (inputStream == null) return "";
        StringBuilder text = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                text.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return text.toString();
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
                // TODO: Allow user to set content-type
                byte[] content = params.toEncodedString().getBytes();
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + charset);
                urlConnection.setRequestProperty("Content-Length", Long.toString(content.length));
                try (OutputStream os = urlConnection.getOutputStream()) {
                    os.write(content);
                }
            }

            // Response
            int responseCode = urlConnection.getResponseCode();
            String responseMessage = urlConnection.getResponseMessage();

            // Response Headers
            Map<String, List<String>> responseHeaders = urlConnection.getHeaderFields();

            // Build response object
            HttpResponse response = new HttpResponse();
            response.setUrl(urlConnection.getURL().toString());
            response.setStatusCode(responseCode);
            response.setStatusMessage(responseMessage);
            response.setHeaders(responseHeaders);

            // 'Successful' response codes will be in interval [200,300)
            if (responseCode >= 200 && responseCode < 300) {
                String responseContent = readStream(urlConnection.getInputStream());
                response.setContent(responseContent);
                handler.onSuccess(response);
            } else {
                String responseContent = readStream(urlConnection.getErrorStream());
                response.setContent(responseContent);
                handler.onFailure(response);
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

    public String getUserAgent() {
        return headers.get("User-Agent");
    }

    public void setUserAgent(String userAgent) {
        headers.put("User-Agent", userAgent);
    }

    public void setCharset(String charset) {
        this.charset = charset;
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
