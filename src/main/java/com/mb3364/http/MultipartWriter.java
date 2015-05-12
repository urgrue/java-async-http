package com.mb3364.http;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Writes multipart HTTP data to an OutputStream. Used for uploading files and sending form data.
 */
public class MultipartWriter {

    private static final String EOL = "\r\n";
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    private OutputStream outputStream;
    private PrintWriter writer;

    private RequestParams requestParams;
    private String boundary;
    private String charset;

    public static void write(HttpURLConnection urlConnection, RequestParams requestParams) throws IOException {
        MultipartWriter mpw = new MultipartWriter(urlConnection, requestParams);
        mpw.writeParts();
    }

    private MultipartWriter(HttpURLConnection urlConnection, RequestParams requestParams) throws IOException {
        this.requestParams = requestParams;
        this.outputStream = urlConnection.getOutputStream();
        this.boundary = "===" + System.currentTimeMillis() + "===";

        this.charset = requestParams.getCharset().name();
        if (!charsetSupported(this.charset)) {
            this.charset = DEFAULT_CHARSET.name();
        }

        urlConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

        try {
            writer = new PrintWriter(new OutputStreamWriter(outputStream, charset), true);
        } catch (UnsupportedEncodingException ignored) {/* Will always succeed. */}
    }

    private void writeParts() throws IOException {
        // Write fields
        for (ConcurrentHashMap.Entry<String, String> param : requestParams.stringEntrySet()) {
            add(param.getKey(), param.getValue());
        }
        for (ConcurrentHashMap.Entry<String, File> param : requestParams.fileEntrySet()) {
            add(param.getKey(), param.getValue());
        }
        // Finish up
        writer.append(EOL).flush();
        writer.append("--").append(boundary).append("--").append(EOL);
        writer.close();
    }

    private void add(String name, String value) {
        writer.append("--").append(boundary).append(EOL);
        writer.append("Content-Disposition: form-data; name=\"").append(name).append("\"").append(EOL);
        writer.append("Content-Type: text/plain; charset=").append(charset).append(EOL);
        writer.append(EOL);
        writer.append(value).append(EOL);
        writer.flush();
    }

    private void add(String name, File file) throws IOException {
        String fileName = file.getName();
        writer.append("--").append(boundary).append(EOL);
        writer.append("Content-Disposition: form-data; name=\"").append(name).append("\"; filename=\"").append(fileName).append("\"").append(EOL);
        writer.append("Content-Type: ").append(URLConnection.guessContentTypeFromName(fileName)).append(EOL);
        writer.append("Content-Transfer-Encoding: binary").append(EOL);
        writer.append(EOL);
        writer.flush();
        // Send file
        Files.copy(file.toPath(), outputStream);
        outputStream.flush();
        writer.append(EOL);
        writer.flush();
    }

    private static boolean charsetSupported(String name) {
        return Charset.availableCharsets().keySet().contains(name);
    }
}
