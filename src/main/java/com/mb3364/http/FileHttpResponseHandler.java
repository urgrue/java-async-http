package com.mb3364.http;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * The response handler for the HTTP request when requesting a file download. This class is meant to
 * be used as an anonymous inner class when making the HTTP request and will return the content as a
 * {@link File} object.
 * <p />
 * Example:
 * <pre>
 * String url = "https://example.org/cool-file.zip";
 * File file = new File("C:\\cool-file.zip"); // Save path
 * HttpClient client = new AsyncHttpClient();
 *
 * client.get(url, params, new FileHttpResponseHandler(file) {
 *     &#064;Override
 *     public void onSuccess(int statusCode, Map<String, List<String>> headers, File content) {
 *          // Request was successful
 *     }
 *
 *     &#064;Override
 *     public void onFailure(int statusCode, Map<String, List<String>> headers, File content) {
 *          // Server responded with a status code 4xx or 5xx error
 *     }
 *
 *     &#064;Override
 *     public void onFailure(Throwable throwable) {
 *          // An exception occurred during the request. Usually unable to connect or there was an error reading the response
 *     }
 *
 *     &#064;Override
 *     public void onProgressChanged(long bytesReceived, long totalBytes) {
 *          // Optional: Track download progress. Will be called several times during file download
 *          System.out.println("Downloaded: " + bytesReceived + " / " + totalBytes);
 *     }
 * });
 * </pre>
 *
 * @see HttpResponseHandler
 * @see StringHttpResponseHandler
 *
 * @author Matthew Bell
 */
public abstract class FileHttpResponseHandler extends HttpResponseHandler {

    private File file; // File containing downloaded file

    public FileHttpResponseHandler(File file) {
        this.file = file;
    }

    @Override
    public void onSuccess(int statusCode, Map<String, List<String>> headers, byte[] content) {
        onSuccess(statusCode, headers, file);
    }

    @Override
    public void onFailure(int statusCode, Map<String, List<String>> headers, byte[] content) {
        // Only part of the file was downloaded
        onFailure(statusCode, headers, file);
    }

    public abstract void onSuccess(int statusCode, Map<String, List<String>> headers, File file);

    public abstract void onFailure(int statusCode, Map<String, List<String>> headers, File file);

    @Override
    public abstract void onFailure(Throwable throwable);

    /**
     * Reads the body content and writes it to the specified file.
     *
     * @param inputStream the {@link InputStream} of the response body
     * @param length the total length of the response body
     * @return <code>null</code> since the content was written to the file
     * @throws IOException if an error occurs while reading the content or writing the file
     */
    @Override
    protected byte[] readFrom(InputStream inputStream, long length) throws IOException {
        FileOutputStream fos = new FileOutputStream(file);
        byte[] buffer = new byte[BUFFER_SIZE];
        long totalBytesRead = 0;
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer, 0, buffer.length)) != -1) {
            fos.write(buffer, 0, bytesRead);
            totalBytesRead += bytesRead;
            onProgressChanged(totalBytesRead, length);
        }
        fos.flush();
        fos.close();
        return null;
    }
}
