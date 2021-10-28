package server.models;

import server.HttpConstants;

import java.util.Map;

public class WebRequest {

    private String method;
    private String path;
    private String version;
    private Map<String, String> headers;

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getVersion() {
        return version;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public boolean isGET() {
        return method.equals(HttpConstants.GET);
    }

    public boolean isPOST() {
        return method.equals(HttpConstants.POST);
    }

    public String getHeader(String key) {
        return headers.getOrDefault(key, null);
    }
}
