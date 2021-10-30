package applications.search;

import applications.search.configuration.SearchConstants;
import applications.search.controller.DataProcessor;
import applications.search.controller.Factory;
import applications.search.models.*;

import java.lang.reflect.Field;

public class TestInitialization {
    public static void initReview() {
        DataProcessor dataProcessor = new DataProcessor();

        InvertedIndex reviewIndex = Factory.getIndex(SearchConstants.Type.REVIEW);

        Review review1 = new Review("12345", "The dog and the cat");
        Review review2 = new Review("67891", "Hello, the sample review");
        ReviewList reviewList = new ReviewList();
        reviewList.add(0, review1);
        reviewList.add(1, review2);

        try {
            Field reviewListField = DataProcessor.class.getDeclaredField("reviewList");
            reviewListField.setAccessible(true);
            reviewListField.set(dataProcessor, reviewList);
        } catch (NoSuchFieldException | IllegalAccessException exception) {
            System.err.println(exception.getMessage());
        }

        reviewIndex.upsert("the", 0);
        reviewIndex.upsert("the", 1);
        reviewIndex.upsert("dog", 0);
        reviewIndex.upsert("and", 0);
        reviewIndex.upsert("cat", 0);
        reviewIndex.upsert("hello", 1);
        reviewIndex.upsert("sample", 1);
        reviewIndex.upsert("review", 1);
    }

    public static void initQA() {
        DataProcessor dataProcessor = new DataProcessor();

        InvertedIndex qaIndex = Factory.getIndex(SearchConstants.Type.QA);

        QA qa1 = new QA("12345", "How are you", "I am fine");
        QA qa2 = new QA("21422", "And, you joined?", "yes!");
        QAList qaList = new QAList();
        qaList.add(0, qa1);
        qaList.add(1, qa2);

        try {
            Field reviewListField = DataProcessor.class.getDeclaredField("qaList");
            reviewListField.setAccessible(true);
            reviewListField.set(dataProcessor, qaList);
        } catch (NoSuchFieldException | IllegalAccessException exception) {
            System.err.println(exception.getMessage());
        }

        qaIndex.upsert("how", 0);
        qaIndex.upsert("are", 0);
        qaIndex.upsert("you", 0);
        qaIndex.upsert("I", 0);
        qaIndex.upsert("am", 0);
        qaIndex.upsert("fine", 0);
        qaIndex.upsert("and", 1);
        qaIndex.upsert("you", 1);
        qaIndex.upsert("joined", 1);
        qaIndex.upsert("yes", 1);
    }

    public static void clean() {
        DataProcessor dataProcessor = new DataProcessor();
        InvertedIndex reviewIndex = Factory.getIndex(SearchConstants.Type.REVIEW);
        InvertedIndex qaIndex = Factory.getIndex(SearchConstants.Type.QA);

        reviewIndex.RemoveAll();
        qaIndex.RemoveAll();

        try {
            Field reviewListField = DataProcessor.class.getDeclaredField("reviewList");
            Field qaListField = DataProcessor.class.getDeclaredField("qaList");

            reviewListField.setAccessible(true);
            qaListField.setAccessible(true);

            reviewListField.set(dataProcessor, new ReviewList());
            qaListField.set(dataProcessor, new QAList());
        }
        catch (NoSuchFieldException | IllegalAccessException exception) {
            System.err.println(exception.getMessage());
        }
    }
}
