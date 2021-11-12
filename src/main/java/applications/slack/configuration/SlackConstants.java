package applications.slack.configuration;

/**
 * Constant value required by Slack Server
 *
 * @author Palak Jain
 */
public class SlackConstants {

    //Port
    public static int PORT = 9090;

    //Path
    public static String SLACK_BOT_URI = "/slackbot";

    public static String SLACK_API_URI = "https://slack.com/api/chat.postMessage";

    //Channel and access token will be updated dynamically based on the configuration
    public static String CHANNEL;
    public static String ACCESS_TOKEN;

    //Headers
    public static String AUTHORIZATION_KEY = "Authorization";
    public static String AUTHORIZATION_VALUE = "Bearer %s";
    public static String CONTENT_TYPE_KEY = "Content-Type";
    public static String CONTENT_TYPE_VALUE = "application/json";

    //JSON request body to Slack APO
    public static String JSON_BODY = "{\n" +
                "\"channel\": \"%s\", \n" +
                "\"text\": \"%s\" \n" +
            "}";

    //Slack error messages
    public static String NO_TOKEN = "invalid_auth";
    public static String NO_CHANNEL = "invalid_arguments";
    public static String WRONG_CHANNEL = "channel_not_found";
    public static String NO_TEXT = "no_text";

    //XHTML pages
    public static final String SLACK_BOT_FORM = """
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
            </html>""";

    public static final String SLACK_BOT_RESPONSE = """
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
            %s\s
            </body>
            </html>""";

    public static void setAccessToken(String accessToken) {
        ACCESS_TOKEN = accessToken;
    }

    public static void setCHANNEL(String CHANNEL) {
        SlackConstants.CHANNEL = CHANNEL;
    }
}
