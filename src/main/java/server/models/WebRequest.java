package server.models;

import server.HttpConstants;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * A request received from a client.
 *
 * @author Palak Jain
 */
public class WebRequest {
    private String method;
    private String path;
    private String version;
    private Map<String, String> headers;
    private BufferedReader reader;

    public WebRequest(BufferedReader reader) {
        this.headers = new HashMap<>();
        this.reader = reader;
    }

    /**
     * @return the method GET or POST
     */
    public String getMethod() {
        return method;
    }

    /**
     * @return the path of a requested page
     */
    public String getPath() {
        return path;
    }

    /**
     * @return HTTP version
     */
    public String getVersion() {
        return version;
    }

    /**
     * Set the method which client made
     * @param method Either GET or POST
     */
    public void setMethod(String method) {
        this.method = method;
    }

    /**
     * Set the path of a requested page
     * @param path A String
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * Set the HTTP version of received request from the client.
     * @param version HTTP version
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * @return true if GET request else false
     */
    public boolean isGET() {
        return method.equals(HttpConstants.GET);
    }

    /**
     * @return true if POST request else false
     */
    public boolean isPOST() {
        return method.equals(HttpConstants.POST);
    }

    /**
     * Get the value of the header at the specified key.
     *
     * @param key Header key value
     * @return value. Default is null if the key doesn't exist.
     */
    public String getHeader(String key) {
        return headers.getOrDefault(key, null);
    }

    /**
     * Add the header.
     *
     * @param key Key
     * @param value Value
     */
    public void addHeader(String key, String value) {
        this.headers.put(key, value);
    }

    /**
     * Set headers received from client
     * @param headers List of key-value pairs.
     */
    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    /**
     * Read the body of a request
     * @param contentLength
     * @return
     * @throws IOException
     */
    public String read(int contentLength) throws IOException {
        char[] bodyArr = new char[contentLength];
        reader.read(bodyArr, 0, bodyArr.length);

        String encodedBody = new String(bodyArr);
        return URLDecoder.decode(encodedBody.substring(encodedBody.indexOf("=") + 1), StandardCharsets.UTF_8.toString());
    }
}
