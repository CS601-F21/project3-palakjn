package applications.slack.controller;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

/**
 * Makes a connection with Slack API.
 *
 * @author Palak Jain
 */
public class Slack {
    private static final Logger logger = (Logger) LogManager.getLogger(Slack.class);

    /**
     * Makes a POST call to Slack API to post a message to a specified channel.
     *
     * @param slackUrl Slack API URL
     * @param headers Required headers
     * @param body Channel and message information
     * @return returns the success/failure response after verifying the response from Slack API
     */
    public String post(String slackUrl, Map<String, String> headers, String body) {
        try {
            logger.printf(Level.INFO, "Making a post call to %s with the body %s", slackUrl, body);

            HttpRequest.Builder builder = HttpRequest.newBuilder(new URI(slackUrl));
            builder = setHeaders(builder, headers);
            HttpRequest request = builder.POST((HttpRequest.BodyPublishers.ofString(body))).build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return response.body();
        }
        catch (URISyntaxException | IOException | InterruptedException exception) {
            logger.printf(Level.ERROR, "Error while making a POST call to %s with the body %s. %s", slackUrl, body, exception.getMessage());
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
}
