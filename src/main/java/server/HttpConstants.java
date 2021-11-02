package server;

import java.util.HashMap;
import java.util.Map;

/**
 * Constant fields required by a Http server.
 *
 * @author Palak Jain
 */
public class HttpConstants {
    public static final String GET = "GET";
    public static final String POST = "POST";

    public static final String VERSION = "HTTP/1.1";
    public static final String CONTENT_LENGTH = "Content-Length:";
    public static final String CONNECTION_CLOSE = "Connection: close";

    public static final String SHUTDOWN_PATH = "/shutdown";

    public static final String PASSCODE = "palakjn";

    public static final String NOT_FOUND_PAGE = """
            <!DOCTYPE html>
            <html xmlns="http://www.w3.org/1999/xhtml">
            <head>
              <title>Resource not found</title>
            </head>
            <body>

              <p>The resource you are looking for was not found.</p>

            </body>
            </html>""";

    public static final String SHUTDOWN_PAGE = """
            <!DOCTYPE html>
            <html xmlns="http://www.w3.org/1999/xhtml">
            <head>
              <title>Review Search</title>
            </head>
            <body>
            <form action="/shutdown" method="post">
            <input type="password" name="passcode" placeholder="Passcode"></input><br />
            <button  type"submit">Shutdown</button>
            </form>
            </body>
            </html>""";

    public static final String SHUTDOWN_RESPONSE = """
            <!DOCTYPE html>
            <html xmlns="http://www.w3.org/1999/xhtml">
            <head>
              <title>Review Search</title>
            </head>
            <body>
            <h3 style="color: green;">%s</h3>
            </form>
            </body>
            </html>""";

    public static final String SHUTDOWN_ERROR_PAGE = """
            <!DOCTYPE html>
            <html xmlns="http://www.w3.org/1999/xhtml">
            <head>
              <title>Review Search</title>
            </head>
            <body>
            <form action="/shutdown" method="post">
            <input type="password" name="passcode" placeholder="Passcode"></input><br />
            <button  type"submit">Shutdown</button>
            </form>
            <h3 style="color: red;">%s</h3>
            </body>
            </html>""";

    public static Map<Integer, String> STATUS_CODE = new HashMap<>();

    static {
        STATUS_CODE.put(200, "200 OK");
        STATUS_CODE.put(404, "404 Not Found");
        STATUS_CODE.put(405, "405 Method Not Allowed");
        STATUS_CODE.put(400, "400 Bad Request");
        STATUS_CODE.put(411, "411 Length Required");
        STATUS_CODE.put(505, "505 HTTP Version Not Supported");
        STATUS_CODE.put(204, "204 No Content");
    }
}
