package applications.search.controller;

import applications.search.configuration.SearchConstants;
import server.HttpConstants;
import server.controller.Handler;
import server.models.WebRequest;
import server.models.WebResponse;
import utils.Strings;

import java.io.IOException;

public class ReviewSearchHandler extends Handler {
    private DataProcessor dataProcessor;

    public ReviewSearchHandler() {
        dataProcessor = new DataProcessor();
    }

    public void doGet(WebRequest request, WebResponse response) {
        response.setStatus(200);
        response.send(SearchConstants.REVIEW_SEARCH_FORM);
    }

    public void doPost(WebRequest request, WebResponse response) throws IOException {
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
