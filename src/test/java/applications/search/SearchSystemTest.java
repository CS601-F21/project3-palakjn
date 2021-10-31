package applications.search;

import applications.ServerUtil;
import applications.search.configuration.SearchConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SearchSystemTest {
    private static SearchApplication searchApplication = new SearchApplication();

    @BeforeAll
    public static void init() {
        //Initialize config file
        initConfig(new SearchConfig("ReviewDatasetForTesting.json", "QADatasetForTesting.json"));

        //Read dataset
        readDataset();

        //Creating a thread which will start the server and listening for requests
        Thread thread = new Thread(SearchSystemTest::startServer);
        thread.start();
    }

    @Test
    public void GET_findPath_returnExpectedPage() {
        //Main thread will make a Get request to a server
        String actual = ServerUtil.doGet(8080,"localhost", "/find");

        String expected = "HTTP/1.1 200 OK\n" +
                "Connection: close \n" +
                "\n" +
                "<!DOCTYPE html>\n" +
                "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
                "<head>\n" +
                "  <title>Find</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "<form class=\"form\" action=\"/find\" method=\"post\">\n" +
                "<input type=\"text\" name=\"asin\" placeholder=\"ASIN Number\"></input><br />\n" +
                "<button  type\"submit\">Search</button>\n" +
                "</form>\n" +
                "</body>\n" +
                "</html>\n";

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void GET_reviewPath_returnExpectedPage() {
        //Main thread will make a Get request to a server
        String actual = ServerUtil.doGet(8080,"localhost", "/reviewsearch");

        String expected = "HTTP/1.1 200 OK\n" +
                "Connection: close \n" +
                "\n" +
                "<!DOCTYPE html>\n" +
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
                "</html>\n";

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void GET_wrongPath_return405NotFound() {
        //Main thread will make a Get request to a server
        String actual = ServerUtil.doGet(8080, "localhost", "/qasearch");

        String expected = "HTTP/1.1 404 Not Found\n" +
                "Connection: close \n" +
                "\n" +
                "<!DOCTYPE html>\n" +
                "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
                "<head>\n" +
                "  <title>Resource not found</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "\n" +
                "  <p>The resource you are looking for was not found.</p>\n" +
                "\n" +
                "</body>\n" +
                "</html>\n";

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void POST_findPath_validASIN_returnResponse() {
        String body = ServerUtil.getBody("asin","120401325X");

        String actual = ServerUtil.doPost(8080,"localhost", "/find", body);

        String expected = "HTTP/1.1 200 OK\n" +
                "Connection: close \n" +
                "\n" +
                "<!DOCTYPE html>\n" +
                "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
                "<head>\n" +
                "  <title>Find</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "<form class=\"form\" action=\"/find\" method=\"post\">\n" +
                "<input type=\"text\" name=\"asin\" placeholder=\"ASIN Number\"></input><br />\n" +
                "<button  type\"submit\">Search</button>\n" +
                "</form><br /><br /><br />\n" +
                "<h3>Asin number: 120401325X. Reviews: </h3><br />\n" +
                "<p>1) ReviewerID: APX47D16JOP7H, ReviewerName: RLH, ReviewerText: se make using  home button easy. My daughter and I both like m.  I would purchase m again. Well worth  price.</p>\n" +
                "<h3>Asin number: 120401325X. Questions & Answers: </h3><br />\n" +
                "<p>1) Question: can in it be used abroad with a different carrier?, Answer: Yes</p>\n" +
                "<p>2) Question: Is this phone brand new and NOT a mini?, Answer:  phone we received was exactly as described - a brand new Samsung SIII I747, AT&T branded but unlocked (and not a mini). It has worked perfectly on our AT&T service in USA for over a year (4G where available). I suggest you email  vendor and get a response email from m confirming  exact item you will receive.</p>\n" +
                " \n" +
                "</body>\n" +
                "</html>\n";

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void POST_reviewPath_validTerm_returnResponse() {
        String body = ServerUtil.getBody("term","great");

        String actual = ServerUtil.doPost(8080,"localhost", "/reviewsearch", body);

        String expected = "HTTP/1.1 200 OK\n" +
                "Connection: close \n" +
                "\n" +
                "<!DOCTYPE html>\n" +
                "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
                "<head>\n" +
                "  <title>Review Search</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "<form class=\"form\" action=\"/reviewsearch\" method=\"post\">\n" +
                "<input type=\"text\" name=\"term\" placeholder=\"Term\"></input><br />\n" +
                "<button  type\"submit\">Search</button>\n" +
                "</form><br /><br /><br />\n" +
                "<p>1) ASIN Number: 1204013256. ReviewerID: ASY55RVNIL0UD, ReviewerName: emily l., ReviewerText: se stickers work like  review says y do. y stick on great and y stay on  phone. y are super stylish and I can share m with my sister. :).</p>\n" +
                "<p>2) ASIN Number: 1204013256. ReviewerID: A2TMXE2AFO7ONB, ReviewerName: Erica, ReviewerText: se are awesome and make my phone look so stylish! I have only used one so far and have had it on for almost a year! CAN YOU BELIEVE THAT! ONE YEAR!! Great quality!.</p>\n" +
                "<p>3) ASIN Number: 1204013256. ReviewerID: AWJ0WZQYMYFQ4, ReviewerName: JM, ReviewerText: Item arrived in great time and was in perfect condition. However, I ordered se buttons because y were a great deal and included a FREE screen protector. I never received one. Though its not a big deal, it would've been nice to get it since y claim it comes with one..</p>\n" +
                "<p>4) ASIN Number: 120401325. ReviewerID: ATX7CZYFXI1KW, ReviewerName: patrice m rogoza, ReviewerText: awesome! stays on, and looks great. can be used on multiple apple products.  especially having nails, it helps to have an elevated key..</p>\n" +
                " \n" +
                "</body>\n" +
                "</html>\n";

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void POST_reviewPath_specialCharTerm_returnResponse() {
        String body = ServerUtil.getBody("term","fr!ee");

        String actual = ServerUtil.doPost(8080,"localhost", "/reviewsearch", body);

        String expected = "HTTP/1.1 200 OK\n" +
                "Connection: close \n" +
                "\n" +
                "<!DOCTYPE html>\n" +
                "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
                "<head>\n" +
                "  <title>Review Search</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "<form class=\"form\" action=\"/reviewsearch\" method=\"post\">\n" +
                "<input type=\"text\" name=\"term\" placeholder=\"Term\"></input><br />\n" +
                "<button  type\"submit\">Search</button>\n" +
                "</form><br /><br /><br />\n" +
                "<p>1) ASIN Number: 1204013256. ReviewerID: AWJ0WZQYMYFQ4, ReviewerName: JM, ReviewerText: Item arrived in great time and was in perfect condition. However, I ordered se buttons because y were a great deal and included a FREE screen protector. I never received one. Though its not a big deal, it would've been nice to get it since y claim it comes with one..</p>\n" +
                " \n" +
                "</body>\n" +
                "</html>\n";

        Assertions.assertEquals(expected, actual);
    }

    private static void initConfig(SearchConfig searchConfig) {
        try {
            Field configField = SearchApplication.class.getDeclaredField("configuration");
            configField.setAccessible(true);
            configField.set(searchApplication, searchConfig);
        }
        catch (NoSuchFieldException | IllegalAccessException exception) {
            System.err.println(exception.getMessage());
        }
    }

    private static void readDataset() {
        try {
            Method processMethod = SearchApplication.class.getDeclaredMethod("readDataset");
            processMethod.setAccessible(true);
            boolean isValid = (boolean) processMethod.invoke(searchApplication);

            Assertions.assertTrue(isValid);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException exception) {
            System.err.println(exception.getMessage());
        }
    }

    private static void startServer() {
        try {
            Method processMethod = SearchApplication.class.getDeclaredMethod("startServer");
            processMethod.setAccessible(true);
            boolean isValid = (boolean) processMethod.invoke(searchApplication);

            Assertions.assertTrue(isValid);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException exception) {
            System.err.println(exception.getMessage());
        }
    }
}
