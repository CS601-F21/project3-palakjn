package applications.search.controller;

import applications.search.TestInitialization;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import server.models.WebRequest;
import server.models.WebResponse;

import java.io.*;

public class ReviewSearchHandlerTest {
    private static ReviewSearchHandler reviewSearchHandler;

    @BeforeAll
    public static void init() {
        reviewSearchHandler = new ReviewSearchHandler();
        TestInitialization.initReview();
        TestInitialization.initQA();
    }

    @Test
    public void doGet_validInputs_sendValidResponse() {
        WebRequest webRequest = new WebRequest();
        StringWriter actualStringWriter = new StringWriter();
        WebResponse webResponse = new WebResponse(new PrintWriter(actualStringWriter), null);

        reviewSearchHandler.doGet(webRequest, webResponse);

        StringWriter expectedStringWriter = new StringWriter();
        PrintWriter expectedPrintWriter = new PrintWriter(expectedStringWriter);
        expectedPrintWriter.printf("HTTP/1.1 200 OK\r\n");
        expectedPrintWriter.printf("Connection: close \r\n\r\n");
        expectedPrintWriter.println("<!DOCTYPE html>\n" +
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
                "</html>");

        Assertions.assertEquals(expectedStringWriter.toString(), actualStringWriter.toString());
    }

    @Test
    public void doPost_withNoHeader_send411() {
        WebRequest webRequest = new WebRequest();
        StringWriter actualStringWriter = new StringWriter();
        WebResponse webResponse = new WebResponse(new PrintWriter(actualStringWriter), null);

        try {
            reviewSearchHandler.doPost(webRequest, webResponse);

            StringWriter expectedStringWriter = new StringWriter();
            PrintWriter expectedPrintWriter = new PrintWriter(expectedStringWriter);
            expectedPrintWriter.printf("HTTP/1.1 411 Length Required\r\n");
            expectedPrintWriter.printf("Connection: close \r\n\r\n");

            Assertions.assertEquals(expectedStringWriter.toString(), actualStringWriter.toString());
        } catch (IOException ioException) {
            System.err.println(ioException.getMessage());
        }
    }

    @Test
    public void doPost_validInput_send200() {
        WebRequest webRequest = new WebRequest();
        StringWriter actualStringWriter = new StringWriter();
        BufferedReader reader = new BufferedReader(new StringReader("the"));
        WebResponse webResponse = new WebResponse(new PrintWriter(actualStringWriter), reader);
        webRequest.addHeader("Content-Length:", "3");

        try {
            reviewSearchHandler.doPost(webRequest, webResponse);

            StringWriter expectedStringWriter = new StringWriter();
            PrintWriter expectedPrintWriter = new PrintWriter(expectedStringWriter);
            expectedPrintWriter.printf("HTTP/1.1 200 OK\r\n");
            expectedPrintWriter.printf("Connection: close \r\n\r\n");
            expectedPrintWriter.println("<!DOCTYPE html>\n" +
                    "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
                    "<head>\n" +
                    "  <title>Review Search</title>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "<form class=\"form\" action=\"/reviewsearch\" method=\"post\">\n" +
                    "<input type=\"text\" name=\"term\" placeholder=\"Term\"></input><br />\n" +
                    "<button  type\"submit\">Search</button>\n" +
                    "</form><br /><br /><br />\n" +
                    "<p>1) ASIN Number: 12345. ReviewerID: null, ReviewerName: null, ReviewerText: The dog and the cat.</p>\n" +
                    "<p>2) ASIN Number: 67891. ReviewerID: null, ReviewerName: null, ReviewerText: Hello, the sample review.</p>\n \n" +
                    "</body>\n" +
                    "</html>");

            Assertions.assertEquals(expectedStringWriter.toString(), actualStringWriter.toString());
        } catch (IOException ioException) {
            System.err.println(ioException.getMessage());
        }
    }

    @AfterAll
    public static void clean() {
        TestInitialization.clean();
    }
}
