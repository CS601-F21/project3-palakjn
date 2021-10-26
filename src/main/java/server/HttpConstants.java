package server;

public class HttpConstants {
    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String VERSION = "HTTP/1.0";

    public static final String OK = "200 OK";
    public static final String NOT_FOUND = "404 Not Found";
    public static final String NOT_ALLOWED = "405 Method Not Allowed";
    public static final String BAD_REQUEST = "400 Bad Request";
    public static final String LENGTH_REQUIRED = "411 Length Required";
    public static final String VERSION_NOT_SUPPORTED = "505 HTTP Version Not Supported";
    public static final String NO_CONTENT = "204 No Content";

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
}
