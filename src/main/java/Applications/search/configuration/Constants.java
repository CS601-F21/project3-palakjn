package applications.search.configuration;

public class Constants {

    //Port
    public static int PORT = 8080;

    //Path
    public static String REVIEW_SERACH_URI = "/reviewsearch";
    public static String FIND_URI = "/find";

    public enum Type {
        REVIEW,
        QA
    }

    //Choices from End User
    public static String FIND = "find";
    public static String REVIEWSEARCH = "reviewsearch";
    public static String QASEARCH = "qasearch";
    public static String REVIEWPARTIALSEARCH = "reviewpartialsearch";
    public static String QAPARTIALSEARCH = "qapartialsearch";
    public static String EXIT = "exit";

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
            "{0} \n" +
            "</body>\n" +
            "</html>";

    public static final String FIND_ASIN_RESPONSE = "<!DOCTYPE html>\n" +
            "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
            "<head>\n" +
            "  <title>Review Search</title>\n" +
            "</head>\n" +
            "<body>\n" +
            "<form class=\"form\" action=\"/reviewsearch\" method=\"post\">\n" +
            "<input type=\"text\" name=\"term\" placeholder=\"Term\"></input><br />\n" +
            "<button  type\"submit\">Search</button>\n" +
            "</form><br /><br /><br />\n" +
            "{0} \n" +
            "</body>\n" +
            "</html>";
}
