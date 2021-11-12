package server.models;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import server.HttpConstants;
import java.io.PrintWriter;

/**
 * A response to send to client based on the received request.
 *
 * @author Palak Jain
 */
public class WebResponse {
    private PrintWriter writer;
    private static final Logger logger = (Logger) LogManager.getLogger(WebResponse.class);

    public WebResponse(PrintWriter writer) {
        this.writer = writer;
    }

    /**
     * Set the status code of a response.
     *
     * @param statusCode Status code
     */
    public void setStatus(int statusCode) {
        if(HttpConstants.STATUS_CODE.containsKey(statusCode)) {
            writer.printf("%s %s\r\n", HttpConstants.VERSION, HttpConstants.STATUS_CODE.get(statusCode));
            writer.printf("%s \r\n\r\n", HttpConstants.CONNECTION_CLOSE);

            if(statusCode == 404) {
                writer.println(HttpConstants.NOT_FOUND_PAGE);
            }
        }
    }

    /**
     * Send the response to a client
     * @param response String response in XHTML format
     */
    public void send(String response) {
       writer.println(response);
    }

    /**
     * Flush the writer.
     */
    public void flush() {
        writer.flush();
    }
}
