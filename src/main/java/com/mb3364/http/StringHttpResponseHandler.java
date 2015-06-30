package com.mb3364.http;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

/**
 * The response handler for the HTTP request when requesting a file download. This class is meant to
 * be used as an anonymous inner class when making the HTTP request and will return the content as a
 * {@link String} object.
 * <p />
 * Example:
 * <pre>
 * client.get(url, params, new FileHttpResponseHandler(file) {
 *     &#064;Override
 *     public void onSuccess(int statusCode, Map<String, List<String>> headers, String content) {
 *          // Request was successful
 *     }
 *
 *     &#064;Override
 *     public void onFailure(int statusCode, Map<String, List<String>> headers, String content) {
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
 * @see HttpResponseHandler
 * @see StringHttpResponseHandler
 *
 * @author Matthew Bell
 */
public abstract class StringHttpResponseHandler extends HttpResponseHandler {

    public static String DEFAULT_CHARSET = "UTF-8";

    /**
     * Returns the Content Charset from the <code>content-type</code> header field.
     *
     * @return the content CharSet of the resource that the URL references, or <code>null</code> if not known.
     */
    private static String extractContentCharset(Map<String, List<String>> headers) {
        List<String> contentTypes = headers.get("Content-Type");
        if (contentTypes != null) {
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
        return DEFAULT_CHARSET; // No content type header, return the default
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
