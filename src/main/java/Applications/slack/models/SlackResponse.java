package applications.slack.models;

import applications.slack.configuration.SlackConstants;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SlackResponse {
    private boolean ok;
    private String error;
    @SerializedName(value = "response_metadata")
    private ResponseMetadata responseMetadata;

    public boolean isOk() {
        return ok;
    }

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

    private class ResponseMetadata {
        private List<String> messages;

        public List<String> getMessages() {
            return messages;
        }
    }
}


