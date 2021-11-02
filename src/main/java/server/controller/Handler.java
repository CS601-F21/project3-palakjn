package server.controller;

import server.models.WebRequest;
import server.models.WebResponse;
import java.io.IOException;

/**
 * Handles request based on the page requested by client
 *
 * @author Palak Jain
 */
public abstract class Handler {

    /**
     * Handles request based on the page requested by client.
     *
     * @param request Web request
     * @param response Web response
     * @throws IOException
     */
    public void handle(WebRequest request, WebResponse response) throws IOException {
        if(request.isGET()) {
            doGet(request, response);
        }
        else if(request.isPOST()) {
            doPost(request, response);
        }
    }

    /**
     * Handles GET request
     * @param request Web Request
     * @param response Web Response
     */
    protected abstract void doGet(WebRequest request, WebResponse response);

    /**
     * Handles POST request
     * @param request Web Request
     * @param response Web Response
     * @throws IOException
     */
    protected abstract void doPost(WebRequest request, WebResponse response) throws IOException;
}
