package applications.search.controller;

import applications.search.TestInitialization;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import server.models.WebRequest;
import server.models.WebResponse;

import java.io.*;

public class FindHandlerTest {
    private static FindHandler findHandler;

    @BeforeAll
    public static void init() {
        findHandler = new FindHandler();
        TestInitialization.initReview();
        TestInitialization.initQA();
    }

    @Test
    public void doGet_ValidInputs_sendValidResponse() {
        WebRequest webRequest = new WebRequest();
        StringWriter actualStringWriter = new StringWriter();
        WebResponse webResponse = new WebResponse(new PrintWriter(actualStringWriter), null);

        findHandler.doGet(webRequest, webResponse);

        StringWriter expectedStringWriter = new StringWriter();
        PrintWriter expectedPrintWriter = new PrintWriter(expectedStringWriter);
        expectedPrintWriter.printf("HTTP/1.1 200 OK\r\n");
        expectedPrintWriter.printf("Connection: close \r\n\r\n");
        expectedPrintWriter.println("<!DOCTYPE html>\n" +
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
                "</html>");

        Assertions.assertEquals(expectedStringWriter.toString(), actualStringWriter.toString());
    }

    @Test
    public void doPost_withNoHeader_send411() {
        WebRequest webRequest = new WebRequest();
        StringWriter actualStringWriter = new StringWriter();
        WebResponse webResponse = new WebResponse(new PrintWriter(actualStringWriter), null);

        try {
            findHandler.doPost(webRequest, webResponse);

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
        BufferedReader reader = new BufferedReader(new StringReader("12345"));
        WebResponse webResponse = new WebResponse(new PrintWriter(actualStringWriter), reader);
        webRequest.addHeader("Content-Length:", "5");

        try {
            findHandler.doPost(webRequest, webResponse);

            StringWriter expectedStringWriter = new StringWriter();
            PrintWriter expectedPrintWriter = new PrintWriter(expectedStringWriter);
            expectedPrintWriter.printf("HTTP/1.1 200 OK\r\n");
            expectedPrintWriter.printf("Connection: close \r\n\r\n");
            expectedPrintWriter.println("<!DOCTYPE html>\n" +
                    "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
                    "<head>\n" +
                    "  <title>Find</title>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "<form class=\"form\" action=\"/find\" method=\"post\">\n" +
                    "<input type=\"text\" name=\"find\" placeholder=\"ASIN Number\"></input><br />\n" +
                    "<button  type\"submit\">Search</button>\n" +
                    "</form><br /><br /><br />\n" +
                    "<h3>Asin number: 12345. Reviews: </h3><br />\n" +
                    "<p>1) ReviewerID: null, ReviewerName: null, ReviewerText: The dog and the cat</p>\n" +
                    "<h3>Asin number: 12345. Questions & Answers: </h3><br />\n" +
                    "<p>1) Question: How are you, Answer: I am fine</p>\n \n" +
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
