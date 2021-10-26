package server;

import java.io.PrintWriter;

public class ServerUtils {
    /**
     * Send the status line of an HTTP 200 OK response.
     * @param writer
     */
    public static void send200(PrintWriter writer) {
        writer.printf("%s %s\r\n", HttpConstants.VERSION, HttpConstants.OK);
        writer.printf("%s \r\n\r\n", HttpConstants.CONNECTION_CLOSE);
    }

    /**
     * Send the status line of an HTTP 404 Not Found response.
     * @param writer
     */
    public static void send404(PrintWriter writer) {
        writer.printf("%s %s\r\n", HttpConstants.VERSION, HttpConstants.NOT_FOUND);
        writer.printf("%s \r\n\r\n", HttpConstants.CONNECTION_CLOSE);
        writer.println(HttpConstants.NOT_FOUND_PAGE);
    }

    /**
     * Send the status line of an HTTP 405 Method Not Allowed response.
     * @param writer
     */
    public static void send405(PrintWriter writer) {
        writer.printf("%s %s\r\n", HttpConstants.VERSION, HttpConstants.NOT_ALLOWED);
        writer.printf("%s \r\n\r\n", HttpConstants.CONNECTION_CLOSE);
    }

    /**
     * Send the status line of an HTTP 400 Bad Request response.
     * @param writer
     */
    public static void send400(PrintWriter writer) {
        writer.printf("%s %s\r\n", HttpConstants.VERSION, HttpConstants.BAD_REQUEST);
        writer.printf("%s \r\n\r\n", HttpConstants.CONNECTION_CLOSE);
    }

    /**
     * Send the status line of an HTTP 4411 Length Required response.
     * @param writer
     */
    public static void send411(PrintWriter writer) {
        writer.printf("%s %s\r\n", HttpConstants.VERSION, HttpConstants.LENGTH_REQUIRED);
        writer.printf("%s \r\n\r\n", HttpConstants.CONNECTION_CLOSE);
    }

    /**
     * Send the status line of an HTTP 505 HTTP Version Not Supported response.
     * @param writer
     */
    public static void send505(PrintWriter writer) {
        writer.printf("%s %s\r\n", HttpConstants.VERSION, HttpConstants.VERSION_NOT_SUPPORTED);
        writer.printf("%s \r\n\r\n", HttpConstants.CONNECTION_CLOSE);
    }

    /**
     * Send the status line of an HTTP 204 No Content response.
     * @param writer
     */
    public static void send204(PrintWriter writer) {
        writer.printf("%s %s\r\n", HttpConstants.VERSION, HttpConstants.NO_CONTENT);
        writer.printf("%s \r\n\r\n", HttpConstants.CONNECTION_CLOSE);
    }
}
