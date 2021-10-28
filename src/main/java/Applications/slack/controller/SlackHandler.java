package applications.slack.controller;

import applications.slack.configuration.SlackConfig;
import applications.slack.configuration.SlackConstants;
import applications.slack.models.SlackResponse;
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

public class SlackHandler extends Handler {
    public void doGet(WebRequest request, WebResponse response) {
        response.setStatus(200);
        response.send(SlackConstants.SLACK_BOT_FORM);
    }

    public void doPost(WebRequest request, WebResponse response) throws IOException {
        int contentLength = 0;
        String header = request.getHeader(HttpConstants.CONTENT_LENGTH);

        if(!Strings.isNullOrEmpty(header)) {
            contentLength = Integer.parseInt(header);

            String messageToSend = response.read(contentLength);
            String messageToDisplay = send(SlackConstants.SLACK_API_URI, getHeaders(), getBody(messageToSend));

            if(!Strings.isNullOrEmpty(messageToDisplay)) {
                response.setStatus(200);
                response.send(String.format(SlackConstants.SLACK_BOT_RESPONSE, messageToDisplay));
            }
            else {
                //No content to display
                response.setStatus(204);
            }
        }
        else {
            response.setStatus(411);
        }
    }

    private String getBody(String message) {
        return String.format(SlackConstants.JSON_BODY, SlackConstants.CHANNEL, message);
    }

    private Map<String, String> getHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put(SlackConstants.AUTHORIZATION_KEY,String.format(SlackConstants.AUTHORIZATION_VALUE, SlackConstants.ACCESS_TOKEN));
        headers.put(SlackConstants.CONTENT_TYPE_KEY, SlackConstants.CONTENT_TYPE_VALUE);

        return headers;
    }

    private String send(String url, Map<String, String> headers, String body) {
        try {
            HttpRequest.Builder builder = HttpRequest.newBuilder(new URI(url));
            builder = setHeaders(builder, headers);
            HttpRequest request = builder.POST((HttpRequest.BodyPublishers.ofString(body))).build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println(response);

            return verifyResponse(response.body());
        }
        catch (URISyntaxException | IOException | InterruptedException exception) {
            System.err.println(exception.getMessage());
            return null;
        }
    }

    private HttpRequest.Builder setHeaders(HttpRequest.Builder builder, Map<String, String> headers) {
        if(headers != null) {
            for (String key : headers.keySet()) {
                builder = builder.setHeader(key, headers.get(key));
            }
        }

        return builder;
    }

    private String verifyResponse(String response) {
        String message = "";

        SlackResponse slackResponse = JsonManager.fromJson(response, SlackResponse.class);

        if(slackResponse != null) {
            if(slackResponse.isOk()) {
                message = String.format("<h3 style=\"color: green;\">Hooray! Message was sent to a channel: %s.</h3>", SlackConstants.CHANNEL);
            }
            else {
                message = slackResponse.getError();
            }
        }

        return message;
    }
}
