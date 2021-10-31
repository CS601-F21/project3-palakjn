package applications.slack;

import applications.ServerUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SlackSystemTest {
    private static SlackApplication slackApplication;

    @BeforeAll
    public static void init() {
        slackApplication = new SlackApplication();
        readConfig();
        Assumptions.assumeTrue(verifyConfig());


        //Creating a thread which will start the server and listening for requests
        Thread thread = new Thread(SlackSystemTest::startServer);
        thread.start();
    }

    @Test
    public void GET_slackBotPath_returnExpectedPage() {
        //Main thread will make a Get request to a server
        String actual = ServerUtil.doGet(9090,"localhost", "/slackbot");

        String expected = """
                HTTP/1.1 200 OK
                Connection: close\s

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
                </html>
                """;

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void GET_wrongPath_return405NotFound() {
        //Main thread will make a Get request to a server
        String actual = ServerUtil.doGet(9090, "localhost", "/slack");

        String expected = """
                HTTP/1.1 404 Not Found
                Connection: close\s

                <!DOCTYPE html>
                <html xmlns="http://www.w3.org/1999/xhtml">
                <head>
                  <title>Resource not found</title>
                </head>
                <body>

                  <p>The resource you are looking for was not found.</p>

                </body>
                </html>
                """;

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void POST_slackBotPath_emptyMessage_returnErrorResponse() {
        String body = ServerUtil.getBody("message","");

        String actual = ServerUtil.doPost(9090,"localhost", "/slackbot", body);

        String expected = """
               HTTP/1.1 200 OK
               Connection: close\s
               
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
               <h3 style="color: red;">Error while sending message to a channel app-testing: no_text</h3>\s
               </body>
               </html>
                """;

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void POST_slackBotPath_validMessage_returnResponse() {
        String body = ServerUtil.getBody("message","Testing!");

        String actual = ServerUtil.doPost(9090,"localhost", "/slackbot", body);

        String expected = """
                HTTP/1.1 200 OK
                Connection: close\s
                               
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
                <h3 style="color: green;">Hooray! Message was sent to a channel: app-testing.</h3>\s
                </body>
                </html>
                """;

        Assertions.assertEquals(expected, actual);
    }

    private static void readConfig() {
        try {
            Method processMethod = SlackApplication.class.getDeclaredMethod("readConfig", String.class);
            processMethod.setAccessible(true);
            processMethod.invoke(slackApplication, "Configuration.json");
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException exception) {
            System.err.println(exception.getMessage());
        }
    }

    private static boolean verifyConfig() {
        try {
            Method processMethod = SlackApplication.class.getDeclaredMethod("verifyConfig");
            processMethod.setAccessible(true);
            return (boolean) processMethod.invoke(slackApplication);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException exception) {
            System.err.println(exception.getMessage());
        }

        return false;
    }

    private static void startServer() {
        try {
            Method processMethod = SlackApplication.class.getDeclaredMethod("startServer");
            processMethod.setAccessible(true);
            boolean isValid = (boolean) processMethod.invoke(slackApplication);

            Assertions.assertTrue(isValid);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException exception) {
            System.err.println(exception.getMessage());
        }
    }
}
