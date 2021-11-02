package applications.slack.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import server.models.WebRequest;
import server.models.WebResponse;

import java.io.*;

public class SlackHandlerTest {
    private static SlackHandler slackHandler;

    @BeforeAll
    public static void init() {
        slackHandler = new SlackHandler();
    }

    @Test
    public void doGet_validInputs_sendValidResponse() {
        WebRequest webRequest = new WebRequest(null);
        StringWriter actualStringWriter = new StringWriter();
        WebResponse webResponse = new WebResponse(new PrintWriter(actualStringWriter));

        slackHandler.doGet(webRequest, webResponse);

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
                <button  type"submit">Send</button>
                </form>
                </body>
                </html>""");

        Assertions.assertEquals(expectedStringWriter.toString(), actualStringWriter.toString());
    }

    @Test
    public void doPost_withNoHeader_send411() {
        WebRequest webRequest = new WebRequest(null);
        StringWriter actualStringWriter = new StringWriter();
        WebResponse webResponse = new WebResponse(new PrintWriter(actualStringWriter));

        try {
            slackHandler.doPost(webRequest, webResponse);

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
    public void doPost_noConfig_send200WithError() {
        BufferedReader reader = new BufferedReader(new StringReader("12345"));
        WebRequest webRequest = new WebRequest(reader);
        StringWriter actualStringWriter = new StringWriter();
        WebResponse webResponse = new WebResponse(new PrintWriter(actualStringWriter));
        webRequest.addHeader("Content-Length:", "5");

        try {
            slackHandler.doPost(webRequest, webResponse);

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
                    <button  type"submit">Send</button>
                    </form><br /><br /><br />
                    <h3 style="color: red;">Error while sending message to a channel null: invalid_auth</h3>\s
                    </body>
                    </html>""");

            Assertions.assertEquals(expectedStringWriter.toString(), actualStringWriter.toString());
        } catch (IOException ioException) {
            System.err.println(ioException.getMessage());
        }
    }
}
