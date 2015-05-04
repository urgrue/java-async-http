package main.java.com.mb3364.http;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpResponse {

    private String url = "";
    private int statusCode = -1;
    private String statusMessage = "";
    private Map<String, List<String>> headers = new HashMap<>();
    private String content = "";

    @Override
    public String toString() {
        return "HttpResponse{" +
                "url='" + url + '\'' +
                ", statusCode=" + statusCode +
                ", statusMessage='" + statusMessage + '\'' +
                ", headers=" + headers +
                ", content='" + content + '\'' +
                '}';
    }

    public String getUrl() {
        return url;
    }

    protected void setUrl(String url) {
        this.url = url;
    }

    public int getStatusCode() {
        return statusCode;
    }

    protected void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    protected void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    protected void setHeaders(Map<String, List<String>> headers) {
        this.headers = headers;
    }

    public String getContent() {
        return content;
    }

    protected void setContent(String content) {
        this.content = content;
    }
}
