package applications.search;

import applications.search.configuration.SearchConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;

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
        String actual = doGet("localhost", "/find");

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
        String actual = doGet("localhost", "/reviewsearch");

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
        String actual = doGet("localhost", "/qasearch");

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
        String body = getBody("asin","120401325X");

        String actual = doPost("localhost", "/find", body);

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
        String body = getBody("term","great");

        String actual = doPost("localhost", "/reviewsearch", body);

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
        String body = getBody("term","fr!ee");

        String actual = doPost("localhost", "/reviewsearch", body);

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

    private static void stopServer() {
        try {
            Method processMethod = SearchApplication.class.getDeclaredMethod("shutdown");
            processMethod.setAccessible(true);
            boolean isValid = (boolean) processMethod.invoke(searchApplication);

            Assertions.assertTrue(isValid);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException exception) {
            System.err.println(exception.getMessage());
        }
    }

    private String doGet(String host, String path) {
        StringBuilder builder = new StringBuilder();
        Socket socket = null;

        //Waiting for server to be up
        do {
            try {
                socket = new Socket("localhost", 8080);
            }
            catch (IOException ioException) {
                sleep(1000);
            }
        } while (socket == null);

        try (
                PrintWriter writer = new PrintWriter(socket.getOutputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ){
            String request = getGETRequest(host, path);
            writer.println(request);
            writer.flush();

            String line = reader.readLine();
            while (line != null) {
                builder.append(line).append("\n");
                line = reader.readLine();
            }
        } catch (IOException ioException) {
            System.err.println(ioException.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException ioException) {
                System.err.println("Exception while closing the socket " + ioException.getMessage());
            }
        }

        return builder.toString();
    }

    private String doPost(String host, String path, String body) {
        StringBuilder builder = new StringBuilder();

        try (
                Socket socket = new Socket("localhost", 8080);
                PrintWriter writer = new PrintWriter(socket.getOutputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ){
            String request = getPOSTRequest(host, path, body.length());
            writer.println(request);
            writer.println(body);
            writer.flush();

            String line = reader.readLine();
            while (line != null) {
                builder.append(line + "\n");
                line = reader.readLine();
            }
        } catch (IOException ioException) {
            System.err.println(ioException.getMessage());
        }

        return builder.toString();
    }

    private String getGETRequest(String host, String path) {
        String request = "GET " + path + " HTTP/1.1" + "\n" //GET request
                + "Host: " + host + "\n" //Host header required for HTTP/1.1
                + "Connection: close\n" //Closing connection
                + "\r\n";
        return request;
    }

    private String getPOSTRequest(String host, String path, int contentLength) {
        String request = "POST " + path + " HTTP/1.1" + "\n" //Post request
                + "Host: " + host + "\n" //Host header required for HTTP/1.1
                + "Content-Length: " + contentLength + "\n" //Content Length
                + "Connection: close\n"; //Closing connection
        return request;
    }

    private String getBody(String key, String value) {
        return String.format("%s=%s", key, value);
    }

    private void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        }
        catch (InterruptedException exception) {
            System.out.printf("Fail to sleep for %d time. %s.\n", milliseconds, exception);
        }
    }
}
