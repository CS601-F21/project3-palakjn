package applications.search.controller;

import applications.search.SearchApplication;
import applications.search.configuration.SearchConstants;
import applications.search.models.*;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import utils.JsonManager;
import utils.Strings;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Insert data into Inverted Index and
 * queries it to display specific information.
 *
 * @author Palak Jain
 */
public class DataProcessor {

    private static ReviewList reviewList = new ReviewList();
    private static QAList qaList = new QAList();
    private static final Logger logger = (Logger) LogManager.getLogger(DataProcessor.class);

    /**
     * Read and parse the JSON file into review object and
     * insert each term, and it's document index reference to Inverted Index
     * @param fileLocation Cell phones and accessories reviews dataset file location in a disk.
     * @return true if reading is successful else false.
     */
    public boolean readReviews(String fileLocation) {
        boolean flag = false;

        try (BufferedReader br = Files.newBufferedReader(Paths.get(fileLocation), StandardCharsets.ISO_8859_1)) {
            int index = reviewList.getSize();
            String line = br.readLine();

            while (line != null) {
                Review review = JsonManager.fromJson(line, Review.class);

                if(review != null && !Strings.isNullOrEmpty(review.getReviewText())) {
                    reviewList.add(index, review);
                    process(review.getReviewText(), index, SearchConstants.Type.REVIEW);
                    index++;
                    flag = true;
                }

                line = br.readLine();
            }
        }
        catch (IOException io) {
            StringWriter writer = new StringWriter();
            io.printStackTrace(new PrintWriter(writer));

            System.out.printf("An error occurred while accessing file at a location %s. %s", fileLocation, writer);
            flag = false;
        }

        logger.printf(Level.INFO, "Read %s reviews", reviewList.getSize());
        return flag;
    }

    /**
     * Read and parse the JSON file into QA object and
     * insert terms, and it's document index reference to Inverted Index
     * @param fileLocation Cell phones and accessories Q/A dataset file location in a disk
     * @return true if reading is successful else false.
     */
    public boolean readQA(String fileLocation) {
        boolean flag = false;

        try (BufferedReader br = Files.newBufferedReader(Paths.get(fileLocation), StandardCharsets.ISO_8859_1)){
            int index = qaList.getSize();
            String line = br.readLine();

            while (line != null) {
                QA qa = JsonManager.fromJson(line, QA.class);

                if(qa != null && !Strings.isNullOrEmpty(qa.getQuestion()) && !Strings.isNullOrEmpty(qa.getAnswer())) {
                    qaList.add(index, qa);
                    process(String.format("%s %s", qa.getQuestion(), qa.getAnswer()), index, SearchConstants.Type.QA);
                    index++;
                    flag = true;
                }

                line = br.readLine();
            }
        }
        catch (IOException io) {
            StringWriter writer = new StringWriter();
            io.printStackTrace(new PrintWriter(writer));

            System.out.printf("An error occurred while accessing file at a location %s. %s", fileLocation, writer);
            flag = false;
        }

        logger.printf(Level.INFO, "Read %s questions and answers", qaList.getSize());
        return flag;
    }

    /**
     * Given the ASIN number of a specific product, will display a list of all reviews for
     * that product and will display a list of all questions and answers about that product.
     * @param asin ASIN number of a product.
     */
    public String findAsin(String asin) {
        StringBuilder stringBuilder = new StringBuilder();

        if(!Strings.isNullOrEmpty(asin)) {
            String output = reviewList.toString(asin);

            if (Strings.isNullOrEmpty(output)) {
                logger.printf(Level.WARN, "No reviews found for the product with ASIN %s", asin);
                stringBuilder.append(String.format("<h3>No reviews found for the product with asin number: %s.</h3><br /> \n", asin));
            } else {
                stringBuilder.append(output);
            }

            output = qaList.toString(asin);

            if (Strings.isNullOrEmpty(output)) {
                logger.printf(Level.WARN, "No questions and answers found for the product with ASIN %s", asin);
                stringBuilder.append(String.format("<h3>No questions and answers found for the product with asin number: %s.</h3><br /> \n", asin));
            } else {
                stringBuilder.append(output);
            }
        }
        else  {
            logger.printf(Level.WARN, "The value of ASIN is null");
            stringBuilder.append("<h3 style=\"color: red;\">No Input was given. Provide \"ASIN\" number. </h3>\n");
        }

        return stringBuilder.toString();
    }

    /**
     * Given a one word term, will display a list of all reviews containing the exact
     * term if "isPartial" value is false else will display a list of all reviews
     * where any word in it contains a partial match of the term.
     * @param term One word term
     */
    public String reviewSearch(String term) {
        StringBuilder stringBuilder = new StringBuilder();

        if(!Strings.isNullOrEmpty(term)) {
            InvertedIndex reviewIndex = Factory.getIndex(SearchConstants.Type.REVIEW);
            List<Integer> documentList = reviewIndex.get(term);

            if (documentList != null && documentList.size() > 0) {
                int numbering = 1;

                for (Integer documentIndex : documentList) {
                    String output = reviewList.toString(documentIndex);

                    if (!Strings.isNullOrEmpty(output)) {
                        stringBuilder.append(String.format("<p>%d) %s.</p>\n", numbering, output));
                        numbering++;
                    }
                }
            } else {
                logger.printf(Level.WARN, "No reviews found having the term %s", term);
                stringBuilder.append(String.format("<h3 style=\"color: red;\">No term i.e. \"%s\" found. </h3> \n", term));
            }
        } else  {
            logger.printf(Level.WARN, "The value of Term is null");
            stringBuilder.append("<h3 style=\"color: red;\">No Input was given. Provide \"TERM\" number. </h3>\n");
        }

        return stringBuilder.toString();
    }

    /**
     * Maps each word in the text to the document having it.
     * @param text Collection of words separated by a space.
     * @param index An index of document having the text.
     * @param type Indicated whether text is from review object or from QA object.
     */
    private void process(String text, int index, SearchConstants.Type type) {
        if(!Strings.isNullOrEmpty(text)) {
            InvertedIndex invertedIndex = Factory.getIndex(type);
            //Making all characters in a string as lowercase.
            text = text.toLowerCase();

            //Splitting text by space
            String[] words = text.split(" ");

            for (int i = 0; i < words.length; i++) {
                //Replacing all non-alphabetic digits with empty character.
                String word = words[i].replaceAll("[^a-z0-9]", "");

                if (!Strings.isNullOrEmpty(word)) {
                    invertedIndex.upsert(word, index);
                }
            }
        }
    }
}
