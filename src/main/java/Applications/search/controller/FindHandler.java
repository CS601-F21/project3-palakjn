package applications.search.controller;

import applications.search.configuration.SearchConstants;
import server.Handler;

public class FindHandler implements Handler {
    private DataProcessor dataProcessor;

    public FindHandler() {
        dataProcessor = new DataProcessor();
    }

    @Override
    public String handle(String data) {
        String output = null;

        if(data == null || data.isBlank() || data.isEmpty()) {
            //Get call
            output = SearchConstants.FIND_FORM;
        }
        else {
            //POST call
            String content = dataProcessor.findAsin(data);
            output = String.format(SearchConstants.FIND_ASIN_RESPONSE, content);
        }

        return  output;
    }
}
