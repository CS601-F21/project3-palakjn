package server;

import java.util.HashMap;
import java.util.Map;

public class HttpConstants {
    public static final String GET = "GET";
    public static final String POST = "POST";

    public static final String VERSION = "HTTP/1.1";
    public static final String CONTENT_LENGTH = "Content-Length:";
    public static final String CONNECTION_CLOSE = "Connection: close";

    public static final String NOT_FOUND_PAGE = "<!DOCTYPE html>\n" +
            "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
            "<head>\n" +
            "  <title>Resource not found</title>\n" +
            "</head>\n" +
            "<body>\n" +
            "\n" +
            "  <p>The resource you are looking for was not found.</p>\n" +
            "\n" +
            "</body>\n" +
            "</html>";

    public static Map<Integer, String> STATUS_CODE = new HashMap<>();

    public HttpConstants() {
        STATUS_CODE.put(200, "200 OK");
        STATUS_CODE.put(404, "404 Not Found");
        STATUS_CODE.put(405, "405 Method Not Allowed");
        STATUS_CODE.put(400, "400 Bad Request");
        STATUS_CODE.put(411, "411 Length Required");
        STATUS_CODE.put(505, "505 HTTP Version Not Supported");
        STATUS_CODE.put(204, "No Content");
    }
}
