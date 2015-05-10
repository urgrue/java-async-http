package com.mb3364.http;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

public abstract class StringHttpResponseHandler extends HttpResponseHandler {

    public static String DEFAULT_CHARSET = "UTF-8";

    /**
     * Returns the Content Charset from the <code>content-type</code> header field.
     *
     * @return the content CharSet of the resource that the URL references, or <code>null</code> if not known.
     */
    private static String extractContentCharset(Map<String, List<String>> headers) {
        List<String> contentTypes = headers.get("Content-Type");
        String contentType = contentTypes.get(0);
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

    /**
     * Returns the encoded byte[] contents into the set encoding.
     *
     * @param content byte array of the response content
     * @param charset charset encoding to create string with
     * @return the encoded content string if charset is known, otherwise an empty string .
     */
    private static String getContentString(byte[] content, String charset) {
        if (content == null || content.length == 0) return "";
        if (charset == null) charset = DEFAULT_CHARSET;

        try {
            return new String(content, charset);
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    @Override
    public void onSuccess(int statusCode, Map<String, List<String>> headers, byte[] content) {
        onSuccess(statusCode, headers, getContentString(content, extractContentCharset(headers)));
    }

    public abstract void onSuccess(int statusCode, Map<String, List<String>> headers, String content);

    @Override
    public void onFailure(int statusCode, Map<String, List<String>> headers, byte[] content) {
        onFailure(statusCode, headers, getContentString(content, extractContentCharset(headers)));
    }

    public abstract void onFailure(int statusCode, Map<String, List<String>> headers, String content);

    @Override
    public abstract void onFailure(Throwable throwable);
}
