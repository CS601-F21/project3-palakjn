package applications.search;

import applications.search.configuration.SearchConstants;
import applications.search.configuration.SearchConfig;
import applications.search.controller.*;
import applications.slack.configuration.SlackConstants;
import configuration.Config;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import server.controller.HTTPServer;
import utils.JsonManager;
import utils.Strings;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * An application which starts a server and allow client to search reviews and Q/A based on term and ASIN number.
 *
 * @author Palak Jain
 */
public class SearchApplication {
    private SearchConfig configuration;
    private DataProcessor dataProcessor;
    private static final Logger logger = (Logger) LogManager.getLogger(SearchApplication.class);

    public SearchApplication() {
        this.dataProcessor = new DataProcessor();
    }

    public static void main(String[] args) {
        SearchApplication searchApplication = new SearchApplication();

        //Getting configuration file
        String configFileLocation = searchApplication.getConfig(args);

        if(!Strings.isNullOrEmpty(configFileLocation)) {
            //Read configuration file
            searchApplication.readConfig(configFileLocation);

            //validate if values are non-empty and files exist at a given location
            boolean isValid = searchApplication.verifyConfig();

            if(isValid) {
                boolean isSuccess = searchApplication.readDataset();

                if(isSuccess) {
                    //Starting server
                    searchApplication.startServer();
                }
            }
        }
    }

    /**
     * Reading reviews and Q/As from file storage
     *
     * @return true if reading is successful else false.
     */
    private boolean readDataset() {
        //Reading review file
        logger.printf(Level.INFO,"Parsing review file %s...", configuration.getReviewDatasetPath());
        boolean isSuccess = dataProcessor.readReviews(configuration.getReviewDatasetPath());

        if(isSuccess) {
            //Reading qa file
            logger.printf(Level.INFO,"Parsing QA file %s...", configuration.getQaDatasetPath());
            isSuccess = dataProcessor.readQA(configuration.getQaDatasetPath());
        }

        return isSuccess;
    }

    /**
     * Starts the server listening on port 8080
     */
    private void startServer() {
        logger.printf(Level.INFO, "Starting the server");
        HTTPServer httpServer = new HTTPServer(SearchConstants.PORT);
        httpServer.addMapping(SearchConstants.REVIEW_SEARCH_URI, new ReviewSearchHandler());
        httpServer.addMapping(SearchConstants.FIND_URI, new FindHandler());
        httpServer.startup();
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
            logger.printf(Level.ERROR,"Invalid Arguments.");
        }

        return configFileLocation;
    }

    /**
     * Parse configuration file.
     * @param configFileLocation location of configuration file
     */
    private void readConfig(String configFileLocation) {
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(configFileLocation))) {
            Config config = JsonManager.fromJson(reader, Config.class);
            if(config != null) {
                configuration = config.getSearch();
            }
        }
        catch (IOException ioException) {
            StringWriter writer = new StringWriter();
            ioException.printStackTrace(new PrintWriter(writer));

            logger.printf(Level.ERROR,"Unable to open configuration file at location %s. %s.", configFileLocation, writer);
        }
    }

    /**
     * Verifies if the config has all the values which is needed and files exist at a given locations.
     * @return true if valid else false
     */
    private boolean verifyConfig() {
        boolean flag = false;

        if(configuration == null) {
            logger.printf(Level.ERROR,"No configuration found.");
        }
        else if(Strings.isNullOrEmpty(configuration.getReviewDatasetPath())) {
            logger.printf(Level.ERROR,"No file location provided for reviews dataset.");
        }
        else if(Strings.isNullOrEmpty(configuration.getQaDatasetPath())) {
            logger.printf(Level.ERROR,"No file location provided for QA dataset.");
        }
        else if(!Files.exists(Paths.get(configuration.getReviewDatasetPath()))) {
            logger.printf(Level.ERROR,"No file found at a location %s.\n", configuration.getReviewDatasetPath());
        }
        else if(!Files.exists(Paths.get(configuration.getQaDatasetPath()))) {
            logger.printf(Level.ERROR,"No file found at a location %s.\n", configuration.getQaDatasetPath());
        }
        else {
            flag = true;
        }

        return flag;
    }
}
