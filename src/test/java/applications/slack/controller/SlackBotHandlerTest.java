package applications.slack.controller;

import applications.slack.Mock;
import applications.slack.SlackTestConstants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import server.models.WebRequest;
import server.models.WebResponse;
import utils.HTMLValidator;

import java.io.*;
import java.util.Arrays;
import java.util.List;

public class SlackBotHandlerTest {
    private static SlackBotHandler slackBotHandler;

    @BeforeAll
    public static void init() {
        slackBotHandler = new SlackBotHandler();
    }

    @Test
    public void doGet_validInputs_sendValidResponse() {
        WebRequest webRequest = new WebRequest(null);
        StringWriter actualStringWriter = new StringWriter();
        WebResponse webResponse = new WebResponse(new PrintWriter(actualStringWriter));

        slackBotHandler.doGet(webRequest, webResponse);

        StringWriter expectedStringWriter = new StringWriter();
        PrintWriter expectedPrintWriter = new PrintWriter(expectedStringWriter);
        expectedPrintWriter.printf("HTTP/1.1 200 OK\r\n");
        expectedPrintWriter.printf("Connection: close \r\n\r\n");
        expectedPrintWriter.println("""
                <!DOCTYPE html>
                <html xmlns="http://www.w3.org/1999/xhtml">
                <head>
                  <title>Slack Bot</title>
                </head>
                <body>
                <form class="form" action="/slackbot" method="post">
                <input type="text" name="message" placeholder="Message"></input><br />
                <button type="submit">Send</button>
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

        slackBotHandler.doGet(webRequest, webResponse);

        isXHTML(actualStringWriter.toString());
    }

    @Test
    public void doPost_withNoHeader_send411() {
        WebRequest webRequest = new WebRequest(null);
        StringWriter actualStringWriter = new StringWriter();
        WebResponse webResponse = new WebResponse(new PrintWriter(actualStringWriter));

        try {
            slackBotHandler.doPost(webRequest, webResponse);

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
    public void doPost_noTokenPass_responseError() {
        WebRequest webRequest = new WebRequest(new BufferedReader(new StringReader("")));
        StringWriter actualStringWriter = new StringWriter();
        WebResponse webResponse = new WebResponse(new PrintWriter(actualStringWriter));
        webRequest.addHeader("Content-Length:", "5");

        try {
            SlackBotHandler slackBotHandler = new SlackBotHandler(Mock.getSlackMock(SlackTestConstants.NO_AUTH_SLACK_RESPONSE));
            slackBotHandler.doPost(webRequest, webResponse);

            StringWriter expectedStringWriter = new StringWriter();
            PrintWriter expectedPrintWriter = new PrintWriter(expectedStringWriter);
            expectedPrintWriter.printf("HTTP/1.1 200 OK\r\n");
            expectedPrintWriter.printf("Connection: close \r\n\r\n");
            expectedPrintWriter.println("""
                    <!DOCTYPE html>
                    <html xmlns="http://www.w3.org/1999/xhtml">
                    <head>
                      <title>Slack Bot</title>
                    </head>
                    <body>
                    <form class="form" action="/slackbot" method="post">
                    <input type="text" name="message" placeholder="Message"></input><br />
                    <button type="submit">Send</button>
                    </form><br /><br /><br />
                    <h3 style="color: red;">Error while posting a message to slack. Authentication failed.</h3>\s
                    </body>
                    </html>""");

            Assertions.assertEquals(expectedStringWriter.toString(), actualStringWriter.toString());
        } catch (IOException ioException) {
            System.err.println(ioException.getMessage());
        }
    }

    @Test
    public void doPost_noTokenPass_validXHTMLResponse() {
        WebRequest webRequest = new WebRequest(new BufferedReader(new StringReader("")));
        StringWriter actualStringWriter = new StringWriter();
        WebResponse webResponse = new WebResponse(new PrintWriter(actualStringWriter));
        webRequest.addHeader("Content-Length:", "5");

        try {
            SlackBotHandler slackBotHandler = new SlackBotHandler(Mock.getSlackMock(SlackTestConstants.NO_AUTH_SLACK_RESPONSE));
            slackBotHandler.doPost(webRequest, webResponse);

            isXHTML(actualStringWriter.toString());
        } catch (IOException ioException) {
            System.err.println(ioException.getMessage());
        }
    }

    @Test
    public void doPost_noChannelPass_responseError() {
        WebRequest webRequest = new WebRequest(new BufferedReader(new StringReader("")));
        StringWriter actualStringWriter = new StringWriter();
        WebResponse webResponse = new WebResponse(new PrintWriter(actualStringWriter));
        webRequest.addHeader("Content-Length:", "5");

        try {
            SlackBotHandler slackBotHandler = new SlackBotHandler(Mock.getSlackMock(SlackTestConstants.NO_CHANNEL_SLACK_RESPONSE));
            slackBotHandler.doPost(webRequest, webResponse);

            StringWriter expectedStringWriter = new StringWriter();
            PrintWriter expectedPrintWriter = new PrintWriter(expectedStringWriter);
            expectedPrintWriter.printf("HTTP/1.1 200 OK\r\n");
            expectedPrintWriter.printf("Connection: close \r\n\r\n");
            expectedPrintWriter.println("""
                    <!DOCTYPE html>
                    <html xmlns="http://www.w3.org/1999/xhtml">
                    <head>
                      <title>Slack Bot</title>
                    </head>
                    <body>
                    <form class="form" action="/slackbot" method="post">
                    <input type="text" name="message" placeholder="Message"></input><br />
                    <button type="submit">Send</button>
                    </form><br /><br /><br />
                    <h3 style="color: red;">Error while posting a message to slack. No channel being provided.</h3>\s
                    </body>
                    </html>""");

            Assertions.assertEquals(expectedStringWriter.toString(), actualStringWriter.toString());
        } catch (IOException ioException) {
            System.err.println(ioException.getMessage());
        }
    }

    @Test
    public void doPost_noChannelPass_validXHTMLResponse() {
        WebRequest webRequest = new WebRequest(new BufferedReader(new StringReader("")));
        StringWriter actualStringWriter = new StringWriter();
        WebResponse webResponse = new WebResponse(new PrintWriter(actualStringWriter));
        webRequest.addHeader("Content-Length:", "5");

        try {
            SlackBotHandler slackBotHandler = new SlackBotHandler(Mock.getSlackMock(SlackTestConstants.NO_CHANNEL_SLACK_RESPONSE));
            slackBotHandler.doPost(webRequest, webResponse);

            isXHTML(actualStringWriter.toString());
        } catch (IOException ioException) {
            System.err.println(ioException.getMessage());
        }
    }

    @Test
    public void doPost_noText_responseError() {
        WebRequest webRequest = new WebRequest(new BufferedReader(new StringReader("")));
        StringWriter actualStringWriter = new StringWriter();
        WebResponse webResponse = new WebResponse(new PrintWriter(actualStringWriter));
        webRequest.addHeader("Content-Length:", "5");

        try {
            SlackBotHandler slackBotHandler = new SlackBotHandler(Mock.getSlackMock(SlackTestConstants.NO_TEXT_SLACK_RESPONSE));
            slackBotHandler.doPost(webRequest, webResponse);

            StringWriter expectedStringWriter = new StringWriter();
            PrintWriter expectedPrintWriter = new PrintWriter(expectedStringWriter);
            expectedPrintWriter.printf("HTTP/1.1 200 OK\r\n");
            expectedPrintWriter.printf("Connection: close \r\n\r\n");
            expectedPrintWriter.println("""
                    <!DOCTYPE html>
                    <html xmlns="http://www.w3.org/1999/xhtml">
                    <head>
                      <title>Slack Bot</title>
                    </head>
                    <body>
                    <form class="form" action="/slackbot" method="post">
                    <input type="text" name="message" placeholder="Message"></input><br />
                    <button type="submit">Send</button>
                    </form><br /><br /><br />
                    <h3 style="color: red;">Provide a message to post to a channel null</h3>\s
                    </body>
                    </html>""");

            Assertions.assertEquals(expectedStringWriter.toString(), actualStringWriter.toString());
        } catch (IOException ioException) {
            System.err.println(ioException.getMessage());
        }
    }

    @Test
    public void doPost_noText_validXHTMLResponse() {
        WebRequest webRequest = new WebRequest(new BufferedReader(new StringReader("")));
        StringWriter actualStringWriter = new StringWriter();
        WebResponse webResponse = new WebResponse(new PrintWriter(actualStringWriter));
        webRequest.addHeader("Content-Length:", "5");

        try {
            SlackBotHandler slackBotHandler = new SlackBotHandler(Mock.getSlackMock(SlackTestConstants.NO_TEXT_SLACK_RESPONSE));
            slackBotHandler.doPost(webRequest, webResponse);

            isXHTML(actualStringWriter.toString());
        } catch (IOException ioException) {
            System.err.println(ioException.getMessage());
        }
    }

    @Test
    public void doPost_wrongChannel_responseError() {
        WebRequest webRequest = new WebRequest(new BufferedReader(new StringReader("")));
        StringWriter actualStringWriter = new StringWriter();
        WebResponse webResponse = new WebResponse(new PrintWriter(actualStringWriter));
        webRequest.addHeader("Content-Length:", "5");

        try {
            SlackBotHandler slackBotHandler = new SlackBotHandler(Mock.getSlackMock(SlackTestConstants.INCORRECT_CHANNEL_SLACK_RESPONSE));
            slackBotHandler.doPost(webRequest, webResponse);

            StringWriter expectedStringWriter = new StringWriter();
            PrintWriter expectedPrintWriter = new PrintWriter(expectedStringWriter);
            expectedPrintWriter.printf("HTTP/1.1 200 OK\r\n");
            expectedPrintWriter.printf("Connection: close \r\n\r\n");
            expectedPrintWriter.println("""
                    <!DOCTYPE html>
                    <html xmlns="http://www.w3.org/1999/xhtml">
                    <head>
                      <title>Slack Bot</title>
                    </head>
                    <body>
                    <form class="form" action="/slackbot" method="post">
                    <input type="text" name="message" placeholder="Message"></input><br />
                    <button type="submit">Send</button>
                    </form><br /><br /><br />
                    <h3 style="color: red;">Error while posting a message to slack. No channel with the name null exist.</h3>\s
                    </body>
                    </html>""");

            Assertions.assertEquals(expectedStringWriter.toString(), actualStringWriter.toString());
        } catch (IOException ioException) {
            System.err.println(ioException.getMessage());
        }
    }

    @Test
    public void doPost_wrongChannel_validXHTMLResponse() {
        WebRequest webRequest = new WebRequest(new BufferedReader(new StringReader("")));
        StringWriter actualStringWriter = new StringWriter();
        WebResponse webResponse = new WebResponse(new PrintWriter(actualStringWriter));
        webRequest.addHeader("Content-Length:", "5");

        try {
            SlackBotHandler slackBotHandler = new SlackBotHandler(Mock.getSlackMock(SlackTestConstants.INCORRECT_CHANNEL_SLACK_RESPONSE));
            slackBotHandler.doPost(webRequest, webResponse);

            isXHTML(actualStringWriter.toString());
        } catch (IOException ioException) {
            System.err.println(ioException.getMessage());
        }
    }

    @Test
    public void doPost_validConfigAndNoError_responseSuccess() {
        WebRequest webRequest = new WebRequest(new BufferedReader(new StringReader("")));
        StringWriter actualStringWriter = new StringWriter();
        WebResponse webResponse = new WebResponse(new PrintWriter(actualStringWriter));
        webRequest.addHeader("Content-Length:", "5");

        try {
            SlackBotHandler slackBotHandler = new SlackBotHandler(Mock.getSlackMock(SlackTestConstants.SLACK_SUCCESS_RESPONSE));
            slackBotHandler.doPost(webRequest, webResponse);

            StringWriter expectedStringWriter = new StringWriter();
            PrintWriter expectedPrintWriter = new PrintWriter(expectedStringWriter);
            expectedPrintWriter.printf("HTTP/1.1 200 OK\r\n");
            expectedPrintWriter.printf("Connection: close \r\n\r\n");
            expectedPrintWriter.println("""
                    <!DOCTYPE html>
                    <html xmlns="http://www.w3.org/1999/xhtml">
                    <head>
                      <title>Slack Bot</title>
                    </head>
                    <body>
                    <form class="form" action="/slackbot" method="post">
                    <input type="text" name="message" placeholder="Message"></input><br />
                    <button type="submit">Send</button>
                    </form><br /><br /><br />
                    <h3 style="color: green;">Hooray! Message was sent to a channel: null.</h3>\s
                    </body>
                    </html>""");

            Assertions.assertEquals(expectedStringWriter.toString(), actualStringWriter.toString());
        } catch (IOException ioException) {
            System.err.println(ioException.getMessage());
        }
    }

    @Test
    public void doPost_validConfigAndNoError_validXHTMLResponse() {
        WebRequest webRequest = new WebRequest(new BufferedReader(new StringReader("")));
        StringWriter actualStringWriter = new StringWriter();
        WebResponse webResponse = new WebResponse(new PrintWriter(actualStringWriter));
        webRequest.addHeader("Content-Length:", "5");

        try {
            SlackBotHandler slackBotHandler = new SlackBotHandler(Mock.getSlackMock(SlackTestConstants.SLACK_SUCCESS_RESPONSE));
            slackBotHandler.doPost(webRequest, webResponse);

            isXHTML(actualStringWriter.toString());
        } catch (IOException ioException) {
            System.err.println(ioException.getMessage());
        }
    }

    @Test
    public void doPost_nullSlackResponse_responseError() {
        WebRequest webRequest = new WebRequest(new BufferedReader(new StringReader("")));
        StringWriter actualStringWriter = new StringWriter();
        WebResponse webResponse = new WebResponse(new PrintWriter(actualStringWriter));
        webRequest.addHeader("Content-Length:", "5");

        try {
            SlackBotHandler slackBotHandler = new SlackBotHandler(Mock.getSlackMock(null));
            slackBotHandler.doPost(webRequest, webResponse);

            StringWriter expectedStringWriter = new StringWriter();
            PrintWriter expectedPrintWriter = new PrintWriter(expectedStringWriter);
            expectedPrintWriter.printf("HTTP/1.1 200 OK\r\n");
            expectedPrintWriter.printf("Connection: close \r\n\r\n");
            expectedPrintWriter.println("""
                    <!DOCTYPE html>
                    <html xmlns="http://www.w3.org/1999/xhtml">
                    <head>
                      <title>Slack Bot</title>
                    </head>
                    <body>
                    <form class="form" action="/slackbot" method="post">
                    <input type="text" name="message" placeholder="Message"></input><br />
                    <button type="submit">Send</button>
                    </form><br /><br /><br />
                    <h3 style="color: red;">An issue while sending a message to slack. Try again!.</h3>\s
                    </body>
                    </html>""");

            Assertions.assertEquals(expectedStringWriter.toString(), actualStringWriter.toString());
        } catch (IOException ioException) {
            System.err.println(ioException.getMessage());
        }
    }

    @Test
    public void doPost_nullSlackResponse_validXHTMLResponse() {
        WebRequest webRequest = new WebRequest(new BufferedReader(new StringReader("")));
        StringWriter actualStringWriter = new StringWriter();
        WebResponse webResponse = new WebResponse(new PrintWriter(actualStringWriter));
        webRequest.addHeader("Content-Length:", "5");

        try {
            SlackBotHandler slackBotHandler = new SlackBotHandler(Mock.getSlackMock(null));
            slackBotHandler.doPost(webRequest, webResponse);

            isXHTML(actualStringWriter.toString());
        } catch (IOException ioException) {
            System.err.println(ioException.getMessage());
        }
    }

    private void isXHTML(String actual) {
        List<String> lines = Arrays.stream(actual.split("\n")).toList();
        actual = String.join("", lines.subList(3, lines.size()));

        Assertions.assertTrue(HTMLValidator.isValid(actual));
    }
}
