package applications.search.controller;

import applications.search.TestInitialization;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import server.models.WebRequest;
import server.models.WebResponse;
import utils.HTMLValidator;

import java.io.*;
import java.util.Arrays;
import java.util.List;

public class FindHandlerTest {
    private static FindHandler findHandler;

    @BeforeAll
    public static void init() {
        findHandler = new FindHandler();
        TestInitialization.initReview();
        TestInitialization.initQA();
    }

    @Test
    public void doGet_validInputs_sendValidResponse() {
        WebRequest webRequest = new WebRequest(null);
        StringWriter actualStringWriter = new StringWriter();
        WebResponse webResponse = new WebResponse(new PrintWriter(actualStringWriter));

        findHandler.doGet(webRequest, webResponse);

        StringWriter expectedStringWriter = new StringWriter();
        PrintWriter expectedPrintWriter = new PrintWriter(expectedStringWriter);
        expectedPrintWriter.printf("HTTP/1.1 200 OK\r\n");
        expectedPrintWriter.printf("Connection: close \r\n\r\n");
        expectedPrintWriter.println("""
                <!DOCTYPE html>
                <html xmlns="http://www.w3.org/1999/xhtml">
                <head>
                  <title>Find</title>
                </head>
                <body>
                <form class="form" action="/find" method="post">
                <input type="text" name="asin" placeholder="ASIN Number"></input><br />
                <button  type="submit">Search</button>
                </form>
                </body>
                </html>""");

        Assertions.assertEquals(expectedStringWriter.toString(), actualStringWriter.toString());
    }

    @Test
    public void doGet_validInputs_validXHTMLResponse() {
        WebRequest webRequest = new WebRequest(null);
        StringWriter actualStringWriter = new StringWriter();
        WebResponse webResponse = new WebResponse(new PrintWriter(actualStringWriter));

        findHandler.doGet(webRequest, webResponse);

        isXHTML(actualStringWriter.toString());
    }

    @Test
    public void doPost_withNoHeader_send411() {
        WebRequest webRequest = new WebRequest(null);
        StringWriter actualStringWriter = new StringWriter();
        WebResponse webResponse = new WebResponse(new PrintWriter(actualStringWriter));

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
        BufferedReader reader = new BufferedReader(new StringReader("12345"));
        WebRequest webRequest = new WebRequest(reader);
        StringWriter actualStringWriter = new StringWriter();
        WebResponse webResponse = new WebResponse(new PrintWriter(actualStringWriter));
        webRequest.addHeader("Content-Length:", "5");

        try {
            findHandler.doPost(webRequest, webResponse);

            StringWriter expectedStringWriter = new StringWriter();
            PrintWriter expectedPrintWriter = new PrintWriter(expectedStringWriter);
            expectedPrintWriter.printf("HTTP/1.1 200 OK\r\n");
            expectedPrintWriter.printf("Connection: close \r\n\r\n");
            expectedPrintWriter.println("""
                    <!DOCTYPE html>
                    <html xmlns="http://www.w3.org/1999/xhtml">
                    <head>
                      <title>Find</title>
                    </head>
                    <body>
                    <form class="form" action="/find" method="post">
                    <input type="text" name="asin" placeholder="ASIN Number"></input><br />
                    <button  type="submit">Search</button>
                    </form><br /><br /><br />
                    <h3>Asin number: 12345. Reviews: </h3><br />
                    <p>1) ReviewerID: null, ReviewerName: null, ReviewerText: The dog and the cat</p>
                    <h3>Asin number: 12345. Questions and Answers: </h3><br />
                    <p>1) Question: How are you, Answer: I am fine</p>
                    \s
                    </body>
                    </html>""");

            Assertions.assertEquals(expectedStringWriter.toString(), actualStringWriter.toString());
        } catch (IOException ioException) {
            System.err.println(ioException.getMessage());
        }
    }

    @Test
    public void doPost_validInput_validXHTMLResponse() {
        BufferedReader reader = new BufferedReader(new StringReader("12345"));
        WebRequest webRequest = new WebRequest(reader);
        StringWriter actualStringWriter = new StringWriter();
        WebResponse webResponse = new WebResponse(new PrintWriter(actualStringWriter));
        webRequest.addHeader("Content-Length:", "5");

        try {
            findHandler.doPost(webRequest, webResponse);

            isXHTML(actualStringWriter.toString());
        } catch (IOException ioException) {
            System.err.println(ioException.getMessage());
        }
    }

    @AfterAll
    public static void clean() {
        TestInitialization.clean();
    }

    private void isXHTML(String actual) {
        List<String> lines = Arrays.stream(actual.split("\n")).toList();
        actual = String.join("", lines.subList(3, lines.size()));

        Assertions.assertTrue(HTMLValidator.isValid(actual));
    }
}
