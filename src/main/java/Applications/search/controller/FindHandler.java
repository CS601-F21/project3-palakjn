package applications.search.controller;

import applications.search.configuration.SearchConstants;
import server.HttpConstants;
import server.controller.Handler;
import server.models.WebRequest;
import server.models.WebResponse;
import utils.Strings;

import java.io.IOException;

public class FindHandler implements Handler {
    private DataProcessor dataProcessor;

    public FindHandler() {
        dataProcessor = new DataProcessor();
    }

    @Override
    public void handle(WebRequest request, WebResponse response) throws IOException {
        if(request.isGET()) {
            doGet(request, response);
        }
        else if(request.isPOST()) {
            doPost(request, response);
        }
    }

    private void doGet(WebRequest request, WebResponse response) {
        response.setStatus(200);
        response.send(SearchConstants.FIND_FORM);
    }

    private void doPost(WebRequest request, WebResponse response) throws IOException {
        int contentLength = 0;
        String header = request.getHeader(HttpConstants.CONTENT_LENGTH);

        if(!Strings.isNullOrEmpty(header)) {
            contentLength = Integer.parseInt(header);

            String body = response.read(contentLength);
            String content = dataProcessor.findAsin(body);

            if(!Strings.isNullOrEmpty(content)) {
                response.setStatus(200);
                response.send(String.format(SearchConstants.FIND_ASIN_RESPONSE, content));
            }
            else {
                //No content to display
                response.setStatus(204);
            }
        }
        else {
            response.setStatus(411);
        }
    }
}
