package main.java.com.mb3364.http;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class RequestParams {

    public static final String DEFAULT_ENCODING = "UTF-8";

    private ConcurrentHashMap<String, String> params = new ConcurrentHashMap<>();
    private String encoding;

    public RequestParams() {
        encoding = DEFAULT_ENCODING;
    }

    public boolean containsKey(String key) {
        return params.containsKey(key);
    }

    public void put(String key, String value) {
        params.put(key, value);
    }

    public String get(String key) {
        return params.get(key);
    }

    public void remove(String key) {
        params.remove(key);
    }

    public Set<Map.Entry<String, String>> entrySet() {
        return params.entrySet();
    }

    public int size() {
        return params.size();
    }

    public String toEncodedString() {
        try {
            StringBuilder encoded = new StringBuilder();
            for (ConcurrentHashMap.Entry<String, String> param : params.entrySet()) {
                if (encoded.length() > 0) encoded.append("&");
                encoded.append(URLEncoder.encode(param.getKey(), encoding));
                encoded.append("=");
                encoded.append(URLEncoder.encode(param.getValue(), encoding));
            }

            return encoded.toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }
}
