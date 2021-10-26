package applications.search;

import applications.search.configuration.Constants;
import applications.search.controller.FindHandler;
import applications.search.controller.ReviewSearchHandler;
import server.HTTPServer;

public class SearchApplication {

    public static void main(String[] args) {

    }

    private void startServer() {
        HTTPServer server = new HTTPServer(Constants.PORT);
        server.addMapping(Constants.REVIEW_SERACH_URI, new ReviewSearchHandler());
        server.addMapping(Constants.FIND_URI, new FindHandler());
        server.startup();
    }
}
