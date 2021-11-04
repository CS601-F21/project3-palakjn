package applications.slack.controller;

import applications.slack.configuration.SlackConstants;
import applications.slack.models.SlackResponse;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import server.HttpConstants;
import server.controller.Handler;
import server.models.WebRequest;
import server.models.WebResponse;
import utils.JsonManager;
import utils.Strings;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * A server handles GET or POST request for the path /slackbot
 * Posts a message to Slack channel.
 *
 * @author Palak Jain
 */
public class SlackBotHandler extends Handler {
    private static final Logger logger = (Logger) LogManager.getLogger(SlackBotHandler.class);
    private Slack slack;

    public SlackBotHandler() {
        this.slack = new Slack();
    }

    //For system test to pass mock object
    public SlackBotHandler(Slack slack) {
        this.slack = slack;
    }

    /**
     * Handles GET request.
     * Display a form to ask user for a message
     *
     * @param request Web Request
     * @param response Web Response
     */
    public void doGet(WebRequest request, WebResponse response) {
        response.setStatus(200);
        response.send(SlackConstants.SLACK_BOT_FORM);
    }

    /**
     * Handles POST request.
     * Post a message to Slack channel.
     *
     * @param request Web Request
     * @param response Web Response
     * @throws IOException
     */
    public void doPost(WebRequest request, WebResponse response) throws IOException {
        int contentLength = 0;
        String header = request.getHeader(HttpConstants.CONTENT_LENGTH);

        if(!Strings.isNullOrEmpty(header)) {
            contentLength = Integer.parseInt(header);

            //reading body
            String messageToSend = request.read(contentLength);

            //sending message to Slack
            String slackResponse = this.slack.post(SlackConstants.SLACK_API_URI, getHeaders(), getBody(messageToSend));

            String messageToDisplay = verifyResponse(slackResponse);

            if(!Strings.isNullOrEmpty(messageToDisplay)) {
                response.setStatus(200);
                response.send(String.format(SlackConstants.SLACK_BOT_RESPONSE, messageToDisplay));
            }
            else {
                //No content to display
                logger.printf(Level.WARN, "No content to display. Sending status 204 No Content");
                response.setStatus(204);
            }
        }
        else {
            logger.printf(Level.WARN, "No content length information found. Sending status 411 Length Required");
            response.setStatus(411);
        }
    }

    /**
     * Get the body of a POST request to a Slack API
     * @param message Text message
     * @return POST body in JSON format
     */
    private String getBody(String message) {
        return String.format(SlackConstants.JSON_BODY, SlackConstants.CHANNEL, message);
    }

    /**
     * Get the headers required for making a connection with Slack API
     *
     * @return Headers
     */
    private Map<String, String> getHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put(SlackConstants.AUTHORIZATION_KEY,String.format(SlackConstants.AUTHORIZATION_VALUE, SlackConstants.ACCESS_TOKEN));
        headers.put(SlackConstants.CONTENT_TYPE_KEY, SlackConstants.CONTENT_TYPE_VALUE);

        return headers;
    }

    /**
     * Verifies if the response from Slack API is success or failure.
     *
     * @param response The response body received from Slack API
     * @return The success/failure response.
     */
    private String verifyResponse(String response) {
        String message = "";

        SlackResponse slackResponse = JsonManager.fromJson(response, SlackResponse.class);

        if(slackResponse != null) {
            if(slackResponse.isOk()) {
                logger.printf(Level.INFO, "Successfully posted a message to a channel \"%s\"", SlackConstants.CHANNEL);
                message = String.format("<h3 style=\"color: green;\">Hooray! Message was sent to a channel: %s.</h3>", SlackConstants.CHANNEL);
            }
            else {
                logger.printf(Level.ERROR, "Slack API return an error: %s", response);
                message = slackResponse.getError();
            }
        }
        else {
            logger.printf(Level.ERROR, "Unable to read a response from Slack API");
            message = "<h3 style=\"color: red;\">An issue while sending a message to slack. Try again!.</h3>";
        }

        return message;
    }
}
