package applications.search.controller;

import applications.search.configuration.SearchConstants;
import server.HttpConstants;
import server.controller.Handler;
import server.models.HttpRequest;
import server.models.HttpResponse;
import utils.Strings;

import java.io.IOException;

public class ReviewSearchHandler implements Handler {
    private DataProcessor dataProcessor;

    public ReviewSearchHandler() {
        dataProcessor = new DataProcessor();
    }

    @Override
    public void handle(HttpRequest request, HttpResponse response) throws IOException {
        if(request.isGET()) {
            doGet(request, response);
        }
        else if(request.isPOST()) {
            doPost(request, response);
        }
    }

    private void doGet(HttpRequest request, HttpResponse response) {
        response.setStatus(200);
        response.send(SearchConstants.REVIEW_SEARCH_FORM);
    }

    private void doPost(HttpRequest request, HttpResponse response) throws IOException {
        int contentLength = 0;
        String header = request.getHeader(HttpConstants.CONTENT_LENGTH);

        if(!Strings.isNullOrEmpty(header)) {
            contentLength = Integer.parseInt(header);

            String body = response.read(contentLength);
            String content = dataProcessor.reviewSearch(body);

            if(!Strings.isNullOrEmpty(content)) {
                response.setStatus(200);
                response.send(String.format(SearchConstants.REVIEW_SEARCH_RESPONSE, content));
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
