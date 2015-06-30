package com.mb3364.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

/**
 * The response handler for the HTTP request. This class is meant to be used as an anonymous inner class
 * when making the HTTP request and will return the content as a byte array. You may also inherit this
 * class if you want to handle the content in a unique way. For example: {@link FileHttpResponseHandler},
 * {@link StringHttpResponseHandler}.
 * <p />
 * Example:
 * <pre>
 * client.get(url, params, new HttpResponseHandler() {
 *     &#064;Override
 *     public void onSuccess(int statusCode, Map<String, List<String>> headers, byte[] content) {
 *          // Request was successful
 *     }
 *
 *     &#064;Override
 *     public void onFailure(int statusCode, Map<String, List<String>> headers, byte[] content) {
 *          // Server responded with a status code 4xx or 5xx error
 *     }
 *
 *     &#064;Override
 *     public void onFailure(Throwable throwable) {
 *          // An exception occurred during the request. Usually unable to connect or there was an error reading the response
 *     }
 * });
 * </pre>
 *
 * @see FileHttpResponseHandler
 * @see StringHttpResponseHandler
 *
 * @author Matthew Bell
 */
public abstract class HttpResponseHandler {

    protected static int BUFFER_SIZE = 1024 * 8; // Size of the buffer when reading data from output stream

    /**
     * Called after the {@link HttpURLConnection} object is prepared, but before a connection
     * or the request is actually made. This method is meant to be overridden and is best
     * used if any additional changes need to be made to the {@link HttpURLConnection} object
     * before making the request.
     *
     * @param httpURLConnection the {@link HttpURLConnection} object that will be used for the request
     */
    public void onStart(HttpURLConnection httpURLConnection) {
        // Do  nothing by default
    }

    /**
     * Called after the request has finished and all data has been received. This method is
     * the very last callback, coming after any onSuccess() or onFailure() methods.
     *
     * @param httpURLConnection the {@link HttpURLConnection} object that was used for the request
     */
    public void onFinish(HttpURLConnection httpURLConnection) {
        // Do nothing by default
    }

    /**
     * Called when the request was successful and contains response information. This method is meant to be
     * overridden in an anonymous inner class.
     *
     * @param statusCode the HTTP status code of the response
     * @param headers the HTTP response headers
     * @param content the HTTP response content body
     */
    public abstract void onSuccess(int statusCode, Map<String, List<String>> headers, byte[] content);

    /**
     * Called when the request failed and the server issued an error code. This method is meant to be
     * overridden in an anonymous inner class.
     *
     * @param statusCode the HTTP status code of the response
     * @param headers the HTTP response headers
     * @param content the HTTP response content body
     */
    public abstract void onFailure(int statusCode, Map<String, List<String>> headers, byte[] content);

    /**
     * Called when an irrecoverable exception occurred while processing the response.
     *
     * @param throwable the {@link Throwable} that occurred
     */
    public abstract void onFailure(Throwable throwable);

    /**
     * Called every time the buffer fills while reading the response content. Best used when
     * downloading large files or reading a lot of content.
     *
     * @param bytesReceived the number of bytes that have already been read
     * @param totalBytes the total number of bytes that will be read
     */
    public void onProgressChanged(long bytesReceived, long totalBytes) {
        // Default, do nothing.
    }

    /**
     * Reads from the response body {@link InputStream}. This method can be overridden if you need to
     * read the content in a different way.
     *
     * @param inputStream the {@link InputStream} of the response body
     * @param length the total length of the response body
     * @return a byte array representing the content body
     * @throws IOException if an exception occurs while reading the content body
     */
    protected byte[] readFrom(InputStream inputStream, long length) throws IOException {
        if (inputStream == null) return new byte[0];

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer, 0, buffer.length)) != -1) {
            os.write(buffer, 0, bytesRead);
            onProgressChanged(bytesRead, length);
        }
        os.flush();
        os.close();
        return os.toByteArray();
    }

    /**
     * Processes the response from the HTTP request and makes the appropriate callbacks.
     *
     * @param connection the {@link HttpURLConnection} object that was used for the request
     */
    protected void processResponse(HttpURLConnection connection) {
        try {
            // Response
            int responseCode = connection.getResponseCode();
            long contentLength = connection.getContentLength();
            Map<String, List<String>> responseHeaders = connection.getHeaderFields();

            // 'Successful' response codes will be in interval [200,300)
            if (responseCode >= 200 && responseCode < 300) {
                byte[] responseContent = readFrom(connection.getInputStream(), contentLength);
                onSuccess(responseCode, responseHeaders, responseContent);
            } else {
                byte[] responseContent = readFrom(connection.getErrorStream(), contentLength);
                onFailure(responseCode, responseHeaders, responseContent);
            }
        } catch (IOException e) {
            onFailure(e);
        }
    }
}
