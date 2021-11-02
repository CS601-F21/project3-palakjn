package applications.search.controller;

import applications.search.configuration.SearchConstants;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import server.HttpConstants;
import server.controller.Handler;
import server.models.WebRequest;
import server.models.WebResponse;
import utils.Strings;
import java.io.IOException;

/**
 * Server handles GET or POST requests for /reviewsearch
 *
 * @author Palak Jain
 */
public class ReviewSearchHandler extends Handler {
    private DataProcessor dataProcessor;
    private static final Logger logger = (Logger) LogManager.getLogger(ReviewSearchHandler.class);

    public ReviewSearchHandler() {
        dataProcessor = new DataProcessor();
    }

    /**
     * Handles GET request.
     * Displays a form to ask user for a term to search
     *
     * @param request Web Request
     * @param response Web Response
     */
    public void doGet(WebRequest request, WebResponse response) {
        response.setStatus(200);
        response.send(SearchConstants.REVIEW_SEARCH_FORM);
    }

    /**
     * Handles POST request.
     * Display reviews which contains a term.
     *
     * @param request Web Request
     * @param response Web Response.
     * @throws IOException
     */
    public void doPost(WebRequest request, WebResponse response) throws IOException {
        int contentLength = 0;
        String header = request.getHeader(HttpConstants.CONTENT_LENGTH);

        if(!Strings.isNullOrEmpty(header)) {
            contentLength = Integer.parseInt(header);

            String body = request.read(contentLength);
            String content = dataProcessor.reviewSearch(body.toLowerCase().replaceAll("[^a-z0-9]", ""));

            if(!Strings.isNullOrEmpty(content)) {
                response.setStatus(200);
                response.send(String.format(SearchConstants.REVIEW_SEARCH_RESPONSE, content));
            }
            else {
                //No content to display
                logger.printf(Level.WARN, "No content to display. Sending status 204 No Content");
                response.setStatus(204);
            }
        }
        else {
            logger.printf(Level.WARN, "No content length information found. Sending status 411 Length Required");
            response.setStatus(411);
        }
    }
}
