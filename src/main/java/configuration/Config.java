package configuration;

import applications.search.configuration.SearchConfig;
import applications.slack.configuration.SlackConfig;

public class Config {
    private SearchConfig search;
    private SlackConfig slack;

    public SearchConfig getSearch() {
        return search;
    }

    public SlackConfig getSlack() {
        return slack;
    }
}
