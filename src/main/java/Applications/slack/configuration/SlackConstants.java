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

    //XHTML pages
    public static final String SLACK_BOT_FORM = "<!DOCTYPE html>\n" +
            "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
            "<head>\n" +
            "  <title>Slack Bot</title>\n" +
            "</head>\n" +
            "<body>\n" +
            "<form class=\"form\" action=\"/slackbot\" method=\"post\">\n" +
            "<input type=\"text\" name=\"message\" placeholder=\"Message\"></input><br />\n" +
            "<button  type\"submit\">Send</button>\n" +
            "</form>\n" +
            "</body>\n" +
            "</html>";

    public static final String SLACK_BOT_RESPONSE = "<!DOCTYPE html>\n" +
            "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
            "<head>\n" +
            "  <title>Slack Bot</title>\n" +
            "</head>\n" +
            "<body>\n" +
            "<form class=\"form\" action=\"/slackbot\" method=\"post\">\n" +
            "<input type=\"text\" name=\"message\" placeholder=\"Message\"></input><br />\n" +
            "<button  type\"submit\">Send</button>\n" +
            "</form><br /><br /><br />\n" +
            "%s \n" +
            "</body>\n" +
            "</html>";

    public static void setAccessToken(String accessToken) {
        ACCESS_TOKEN = accessToken;
    }

    public static void setCHANNEL(String CHANNEL) {
        SlackConstants.CHANNEL = CHANNEL;
    }
}
