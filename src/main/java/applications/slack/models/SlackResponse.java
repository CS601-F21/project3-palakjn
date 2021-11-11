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

    /**
     * @return the status of a POST call
     */
    public boolean isOk() {
        return ok;
    }

    /**
     * @return the error message corresponding to the response received from Slack API
     */
    public String getError() {
        String errorMessage = null;

        if(error.equals(SlackConstants.NO_CHANNEL)) {
            errorMessage = "<h3 style=\"color: red;\">Error while posting a message to slack. No channel being provided.</h3>";
        }
        else if(error.equals(SlackConstants.NO_TEXT)) {
            errorMessage = String.format("<h3 style=\"color: red;\">Provide a message to post to a channel %s</h3>", SlackConstants.CHANNEL);
        }
        else if(error.equals(SlackConstants.WRONG_CHANNEL)) {
            errorMessage = String.format("<h3 style=\"color: red;\">Error while posting a message to slack. No channel with the name %s exist.</h3>", SlackConstants.CHANNEL);
        }
        else if(error.equals(SlackConstants.NO_TOKEN)) {
            errorMessage = "<h3 style=\"color: red;\">Error while posting a message to slack. Authentication failed.</h3>";
        }
        else {
            errorMessage = String.format("<h3 style=\"color: red;\">Error while sending message to a channel %s: %s</h3>", SlackConstants.CHANNEL, error);
        }

        return errorMessage;
    }
}


