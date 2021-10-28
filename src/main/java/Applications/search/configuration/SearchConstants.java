package applications.search.configuration;

public class SearchConstants {

    //Port
    public static int PORT = 8080;

    //Path
    public static String REVIEW_SEARCH_URI = "/reviewsearch";
    public static String FIND_URI = "/find";

    public enum Type {
        REVIEW,
        QA
    }

    //XHTML pages
    public static final String REVIEW_SEARCH_FORM = "<!DOCTYPE html>\n" +
            "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
            "<head>\n" +
            "  <title>Review Search</title>\n" +
            "</head>\n" +
            "<body>\n" +
            "<form class=\"form\" action=\"/reviewsearch\" method=\"post\">\n" +
            "<input type=\"text\" name=\"term\" placeholder=\"Term\"></input><br />\n" +
            "<button  type\"submit\">Search</button>\n" +
            "</form>\n" +
            "</body>\n" +
            "</html>";

    public static final String FIND_FORM = "<!DOCTYPE html>\n" +
            "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
            "<head>\n" +
            "  <title>Find</title>\n" +
            "</head>\n" +
            "<body>\n" +
            "<form class=\"form\" action=\"/find\" method=\"post\">\n" +
            "<input type=\"text\" name=\"find\" placeholder=\"ASIN Number\"></input><br />\n" +
            "<button  type\"submit\">Search</button>\n" +
            "</form>\n" +
            "</body>\n" +
            "</html>";

    public static final String REVIEW_SEARCH_RESPONSE = "<!DOCTYPE html>\n" +
            "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
            "<head>\n" +
            "  <title>Review Search</title>\n" +
            "</head>\n" +
            "<body>\n" +
            "<form class=\"form\" action=\"/reviewsearch\" method=\"post\">\n" +
            "<input type=\"text\" name=\"term\" placeholder=\"Term\"></input><br />\n" +
            "<button  type\"submit\">Search</button>\n" +
            "</form><br /><br /><br />\n" +
            "%s \n" +
            "</body>\n" +
            "</html>";

    public static final String FIND_ASIN_RESPONSE = "<!DOCTYPE html>\n" +
            "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
            "<head>\n" +
            "  <title>Find</title>\n" +
            "</head>\n" +
            "<body>\n" +
            "<form class=\"form\" action=\"/find\" method=\"post\">\n" +
            "<input type=\"text\" name=\"find\" placeholder=\"ASIN Number\"></input><br />\n" +
            "<button  type\"submit\">Search</button>\n" +
            "</form><br /><br /><br />\n" +
            "%s \n" +
            "</body>\n" +
            "</html>";
}
