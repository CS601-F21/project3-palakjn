package applications.slack.models;

import applications.slack.configuration.SlackConstants;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Attributes returned as a response from a POST call to a slack API.
 *
 * @author Palak Jain
 */
public class SlackResponse {
    private boolean ok;
    private String error;
    @SerializedName(value = "response_metadata")
    private ResponseMetadata responseMetadata;

    private class ResponseMetadata {
        private List<String> messages;

        public List<String> getMessages() {
            return messages;
        }
    }

    /**
     * @return the status of a POST call
     */
    public boolean isOk() {
        return ok;
    }

    /**
     * @return the error message received from Slack API
     */
    public String getError() {
        List<String> messages =  responseMetadata.getMessages();
        String errorMessage = null;

        if(messages != null && messages.size() > 0) {
            errorMessage = String.format("<h3 style=\"color: red;\">Error while sending message to a channel %s: %s, Description: %s</h3>", SlackConstants.CHANNEL, error, String.join(", ", messages));
        }
        else {
            errorMessage = String.format("<h3 style=\"color: red;\">Error while sending message to a channel %s: %s</h3>", SlackConstants.CHANNEL, error);
        }

        return errorMessage;
    }
}


