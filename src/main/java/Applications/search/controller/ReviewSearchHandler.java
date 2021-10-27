package applications.search.controller;

import applications.search.configuration.SearchConstants;
import server.Handler;

public class ReviewSearchHandler implements Handler {
    private DataProcessor dataProcessor;

    public ReviewSearchHandler() {
        dataProcessor = new DataProcessor();
    }

    @Override
    public String handle(String data) {
        String output = null;

        if(data == null || data.isBlank() || data.isEmpty()) {
            //Get call
            output = SearchConstants.REVIEW_SEARCH_FORM;
        }
        else {
            String content = dataProcessor.reviewSearch(data);
            output = String.format(SearchConstants.REVIEW_SEARCH_RESPONSE, content);
        }

        return  output;
    }
}
