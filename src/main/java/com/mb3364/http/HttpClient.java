package com.mb3364.http;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class HttpClient {

    public static final String DEFAULT_USER_AGENT = "Java-Async-Http";
    public static final String DEFAULT_CHARSET = "UTF-8";

    private final Map<String, String> headers;

    private String charset;
    private int connectionTimeout = 20000;
    private int dataRetrievalTimeout = 20000;

    public HttpClient() {
        headers = Collections.synchronizedMap(new LinkedHashMap<String, String>());
        setUserAgent(DEFAULT_USER_AGENT);
        setCharset(DEFAULT_CHARSET);
    }

    /**
     * Read an InputStream and convert it to a String.
     *
     * @param inputStream InputStream to read
     * @return String representing entire InputStream contents
     * @throws IOException if unable to read stream
     */
    private static String readStream(InputStream inputStream) throws IOException {
        return readStream(inputStream, null); // No encoding, so read as binary data
    }

    /**
     * Read an InputStream and convert it to a String.
     *
     * @param inputStream InputStream to read
     * @param charsetName the charset name of the content encoding
     * @return String representing entire InputStream contents
     * @throws IOException if unable to read stream
     */
    private static String readStream(InputStream inputStream, String charsetName) throws IOException {
        if (inputStream == null) return "";

        InputStreamReader reader;
        if (charsetName != null) {
            reader = new InputStreamReader(inputStream, charsetName);
        } else {
            reader = new InputStreamReader(inputStream);
        }

        StringBuilder text = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(reader);
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            text.append(line);
        }
        return text.toString();
    }

    /**
     * Returns the Content Encoding from the <code>content-type</code> header field.
     *
     * @param connection the URLConnection of the resource.
     * @return the content encoding of the resource that the URL references, or <code>null</code> if not known.
     */
    private String extractContentEncoding(URLConnection connection) {
        String contentType = connection.getContentType();
        String charset = null;

        if (contentType != null) {
            for (String param : contentType.replace(" ", "").split(";")) {
                if (param.startsWith("charset=")) {
                    charset = param.split("=", 2)[1];
                    break;
                }
            }
        }

        return charset;
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
                urlConnection.setFixedLengthStreamingMode(content.length); // Stream the data so we don't run out of memory
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

            // Response Content Encoding
            String contentEncoding = extractContentEncoding(urlConnection);

            // 'Successful' response codes will be in interval [200,300)
            if (responseCode >= 200 && responseCode < 300) {
                String responseContent = readStream(urlConnection.getInputStream(), contentEncoding);
                response.setContent(responseContent);
                handler.onSuccess(response);
            } else {
                String responseContent = readStream(urlConnection.getErrorStream(), contentEncoding);
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

    public void removeHeader(String name) {
        headers.remove(name);
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
