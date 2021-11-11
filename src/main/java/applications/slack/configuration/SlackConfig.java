package applications.slack.configuration;

public class SlackConfig {
    private String accessToken;
    private String channel;

    public SlackConfig(String accessToken, String channel) {
        this.accessToken = accessToken;
        this.channel = channel;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getChannel() {
        return channel;
    }
}
