package applications.search.controller;

import applications.search.configuration.Constants;
import server.Handler;

public class ReviewSearchHandler implements Handler {

    @Override
    public String handle(String data) {
        String output = null;

        if(data == null || data.isBlank() || data.isEmpty()) {
            //Get call
            output = Constants.REVIEW_SEARCH_FORM;
        }
        else {
            //Post call
        }

        return  output;
    }
}
