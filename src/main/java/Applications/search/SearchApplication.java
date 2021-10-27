package applications.search;

import applications.search.configuration.SearchConstants;
import applications.search.configuration.SearchConfig;
import applications.search.controller.DataProcessor;
import applications.search.controller.FindHandler;
import applications.search.controller.ReviewSearchHandler;
import server.HTTPServer;
import utils.JsonManager;
import utils.Strings;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SearchApplication {
    private SearchConfig configuration;
    private DataProcessor dataProcessor;

    public SearchApplication(){
        dataProcessor = new DataProcessor();
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

    private boolean readDataset() {
        //Reading review file
        System.out.printf("Parsing review file %s...\n", configuration.getReviewDatasetPath());
        boolean isSuccess = dataProcessor.readReviews(configuration.getReviewDatasetPath());

        if(isSuccess) {
            //Reading qa file
            System.out.printf("Parsing QA file %s...\n", configuration.getQaDatasetPath());
            isSuccess = dataProcessor.readQA(configuration.getQaDatasetPath());
        }

        return isSuccess;
    }

    private void startServer() {
        HTTPServer server = new HTTPServer(SearchConstants.PORT);
        server.addMapping(SearchConstants.REVIEW_SERACH_URI, new ReviewSearchHandler());
        server.addMapping(SearchConstants.FIND_URI, new FindHandler());
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
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(configFileLocation))){
            configuration = JsonManager.fromJsonToConfig(reader);
        }
        catch (IOException ioException) {
            StringWriter writer = new StringWriter();
            ioException.printStackTrace(new PrintWriter(writer));

            System.out.printf("Unable to open configuration file at location %s. %s. \n", configFileLocation, writer);
        }
    }

    /**
     * Verifies if the config has all the values which is needed and files exist at a given locations.
     * @return true if valid else false
     */
    private boolean verifyConfig() {
        boolean flag = false;

        if(configuration == null) {
            System.out.println("No configuration found.");
        }
        else if(Strings.isNullOrEmpty(configuration.getReviewDatasetPath())) {
            System.out.println("No file location provided for reviews dataset.");
        }
        else if(Strings.isNullOrEmpty(configuration.getQaDatasetPath())) {
            System.out.println("No file location provided for QA dataset.");
        }
        else if(!Files.exists(Paths.get(configuration.getReviewDatasetPath()))) {
            System.out.printf("No file found at a location %s.\n", configuration.getReviewDatasetPath());
        }
        else if(!Files.exists(Paths.get(configuration.getQaDatasetPath()))) {
            System.out.printf("No file found at a location %s.\n", configuration.getQaDatasetPath());
        }
        else {
            flag = true;
        }

        return flag;
    }
}
