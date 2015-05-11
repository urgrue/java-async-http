package com.mb3364.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

public abstract class HttpResponseHandler {

    protected static int BUFFER_SIZE = 1024 * 8;

    public abstract void onSuccess(int statusCode, Map<String, List<String>> headers, byte[] content);

    public abstract void onFailure(int statusCode, Map<String, List<String>> headers, byte[] content);

    public abstract void onFailure(Throwable throwable);

    public void onProgressChanged(long bytesReceived, long totalBytes) {
        // Default, do nothing.
    }

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
