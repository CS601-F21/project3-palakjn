package applications.slack.controller;

import applications.search.controller.FindHandler;
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
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * A server handles GET or POST request for the path /slackbot
 * Posts a message to Slack channel.
 *
 * @author Palak Jain
 */
public class SlackHandler extends Handler {
    private static final Logger logger = (Logger) LogManager.getLogger(SlackHandler.class);

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
            String messageToDisplay = send(SlackConstants.SLACK_API_URI, getHeaders(), getBody(messageToSend));

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
     * Makes a POST call to Slack API to post a message to a specified channel.
     *
     * @param url Slack API URL
     * @param headers Required headers
     * @param body Channel and message information
     * @return returns the success/failure response after verifying the response from Slack API
     */
    private String send(String url, Map<String, String> headers, String body) {
        //TODO: Use HttpsUrlConnection to post message
        //TODO: Create another class handling this so that we can map it.

        try {
            logger.printf(Level.INFO, "Making a post call to %s with the body %s", url, body);

            HttpRequest.Builder builder = HttpRequest.newBuilder(new URI(url));
            builder = setHeaders(builder, headers);
            HttpRequest request = builder.POST((HttpRequest.BodyPublishers.ofString(body))).build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return verifyResponse(response.body());
        }
        catch (URISyntaxException | IOException | InterruptedException exception) {
            logger.printf(Level.ERROR, "Error while making a POST call to %s with the body %s. %s", url, body, exception.getMessage());
            return null;
        }
    }

    /**
     * Set the headers for HttpRequest.Builder
     *
     * @param builder A builder of HttpRequest
     * @param headers Headers to set
     * @return Same builder of HttpRequest
     */
    private HttpRequest.Builder setHeaders(HttpRequest.Builder builder, Map<String, String> headers) {
        if(headers != null) {
            for (String key : headers.keySet()) {
                builder = builder.setHeader(key, headers.get(key));
            }
        }

        return builder;
    }

    /**
     * Verifies if the response from Slack API is success or failure.
     *
     * @param response The response body received from Slack API
     * @return The success/failure response.
     */
    private String verifyResponse(String response) {
        //TODO: Write unit test for this function

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
            message = "An issue while sending a message to slack. Try again!.";
        }

        return message;
    }
}
