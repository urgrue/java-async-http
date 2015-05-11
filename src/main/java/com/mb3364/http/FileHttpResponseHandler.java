package com.mb3364.http;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

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
