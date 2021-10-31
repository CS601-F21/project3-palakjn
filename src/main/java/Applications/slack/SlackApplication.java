package applications.slack;

import applications.slack.configuration.SlackConfig;
import applications.slack.configuration.SlackConstants;
import applications.slack.controller.SlackHandler;
import configuration.Config;
import server.controller.HTTPServer;
import utils.JsonManager;
import utils.Strings;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SlackApplication {
    private SlackConfig configuration;

    public static void main(String[] args) {
        SlackApplication slackApplication = new SlackApplication();

        //Getting configuration file
        String configFileLocation = slackApplication.getConfig(args);

        if(!Strings.isNullOrEmpty(configFileLocation)) {
            //Read configuration file
            slackApplication.readConfig(configFileLocation);

            //validate if values are non-empty
            boolean isValid = slackApplication.verifyConfig();

            if(isValid) {
                //Starting server
                slackApplication.startServer();
            }
        }
    }

    private void startServer() {
        SlackConstants.setCHANNEL(this.configuration.getChannel());
        SlackConstants.setAccessToken(this.configuration.getAccessToken());

        HTTPServer server = new HTTPServer(SlackConstants.PORT);
        server.addMapping(SlackConstants.SLACK_BOT_URI, new SlackHandler());
        server.startup();
    }

    /**
     * Read comment line arguments and return the location of the configuration file.
     * @param args Command line arguments being passed when running a program
     * @return location of configuration file if passed arguments are valid else null
     */
    private String getConfig(String[] args) {
        String configFileLocation = null;

        if(args.length == 2 &&
                args[0].equalsIgnoreCase("-config") &&
                !Strings.isNullOrEmpty(args[1])) {
            configFileLocation = args[1];
        }
        else {
            System.out.println("Invalid Arguments");
        }

        return configFileLocation;
    }

    /**
     * Parse configuration file.
     * @param configFileLocation location of configuration file
     */
    private void readConfig(String configFileLocation) {
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(configFileLocation))) {
            configuration = JsonManager.fromJson(reader, Config.class).getSlack();
        }
        catch (IOException ioException) {
            StringWriter writer = new StringWriter();
            ioException.printStackTrace(new PrintWriter(writer));

            System.out.printf("Unable to open configuration file at location %s. %s. \n", configFileLocation, writer);
        }
    }

    /**
     * Verifies if the config has all the values which is needed.
     * @return true if valid else false
     */
    private boolean verifyConfig() {
        boolean flag = false;

        if(configuration == null) {
            System.out.println("No configuration found.");
        }
        else if(Strings.isNullOrEmpty(configuration.getAccessToken())) {
            System.out.println("Access token is null");
        }
        else if(Strings.isNullOrEmpty(configuration.getChannel())) {
            System.out.println("No channel being provided");
        }
        else {
            flag = true;
        }

        return flag;
    }
}
