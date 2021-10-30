package applications.search.controller;

import applications.search.TestInitialization;
import applications.search.configuration.SearchConstants;
import applications.search.models.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class DataProcessorTest {
    private static DataProcessor dataProcessor = new DataProcessor();
    private static InvertedIndex reviewIndex = Factory.getIndex(SearchConstants.Type.REVIEW);
    private static InvertedIndex qaIndex = Factory.getIndex(SearchConstants.Type.QA);

    @BeforeAll
    public static void init() {
        TestInitialization.initReview();
        TestInitialization.initQA();
    }

    @Test
    public void findAsin_nullInput_returnErrorMessage() {
        String expectedOutput = "<h3 style=\"color: red;\">No Input was given. Provide \"ASIN\" number. </h3>\n";
        String actualOutput = dataProcessor.findAsin(null);

        Assertions.assertEquals(expectedOutput, actualOutput);
    }

    @Test
    public void findAsin_notExistAsin_returnProperMessage() {
        String asin = "93847";
        String expectedOutput = String.format("<h3>No reviews found for the product with asin number: %s.</h3><br /> \n" +
                    "<h3>No questions and answers found for the product with asin number: %s.</h3><br /> \n", asin, asin);
        String actualOutput = dataProcessor.findAsin(asin);

        Assertions.assertEquals(expectedOutput, actualOutput);
    }

    @Test
    public void findAsin_existAsin_returnResults() {
        String asin = "12345";
        String expectedOutput = "<h3>Asin number: 12345. Reviews: </h3><br />\n" +
                    "<p>1) ReviewerID: null, ReviewerName: null, ReviewerText: The dog and the cat</p>\n" +
                    "<h3>Asin number: 12345. Questions & Answers: </h3><br />\n" +
                    "<p>1) Question: How are you, Answer: I am fine</p>\n";
        String actualOutput = dataProcessor.findAsin(asin);

        Assertions.assertEquals(expectedOutput, actualOutput);
    }

    @Test
    public void reviewSearch_nullInput_returnErrorMessage() {
        String expectedOutput = "<h3 style=\"color: red;\">No Input was given. Provide \"TERM\" number. </h3>\n";
        String actualOutput = dataProcessor.reviewSearch(null);

        Assertions.assertEquals(expectedOutput, actualOutput);
    }

    @Test
    public void reviewSearch_notExistTerm_returnProperMessage() {
        String term = "term";
        String expectedOutput = "<h3 style=\"color: red;\">No term i.e. \"term\" found. </h3> \n";
        String actualOutput = dataProcessor.reviewSearch(term);

        Assertions.assertEquals(expectedOutput, actualOutput);
    }

    @Test
    public void reviewSearch_existTerm_returnReviews() {
        String term = "the";
        String expectedOutput = "<p>1) ASIN Number: 12345. ReviewerID: null, ReviewerName: null, ReviewerText: The dog and the cat.</p>\n" +
                                "<p>2) ASIN Number: 67891. ReviewerID: null, ReviewerName: null, ReviewerText: Hello, the sample review.</p>\n";
        String actualOutput = dataProcessor.reviewSearch(term);

        Assertions.assertEquals(expectedOutput, actualOutput);
    }

    @Test
    public void process_shouldRemoveSpecialCharacters() {
        String text = "This text con@tai!ns special ch#aract&ers";

        try {
            Method processMethod = DataProcessor.class.getDeclaredMethod("process", String.class, int.class, SearchConstants.Type.class);
            processMethod.setAccessible(true);
            processMethod.invoke(dataProcessor, text, 3, SearchConstants.Type.REVIEW);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException exception) {
            System.err.println(exception.getMessage());
        }

        reviewIndex = Factory.getIndex(SearchConstants.Type.REVIEW);
        List<Integer> documentRefs = reviewIndex.get("contains");

        Assertions.assertTrue(documentRefs.size() > 0);
    }

    @Test
    public void readQA_validJSON_returnTrue() {
        String qaValidPath = "QADatasetForTesting.json";
        Assertions.assertTrue(dataProcessor.readQA(qaValidPath));
    }

    @Test
    public void readQA_invalidJSON_returnFalse() {
        String qaInValidPath = "QADatasetForTesting - InvalidJSON.json";
        Assertions.assertFalse(dataProcessor.readQA(qaInValidPath));
    }

    @Test
    public void readReviews_validJSON_returnTrue() {
        String reviewValidPath = "ReviewDatasetForTesting.json";
        Assertions.assertTrue(dataProcessor.readReviews(reviewValidPath));
    }

    @Test
    public void readReviews_invalidJSON_returnFalse() {
        String reviewInValidPath = "ReviewDatasetForTesting - InvalidJSON.json";
        Assertions.assertFalse(dataProcessor.readReviews(reviewInValidPath));
    }

    @AfterAll
    public static void clean() {
        TestInitialization.clean();
    }
}
