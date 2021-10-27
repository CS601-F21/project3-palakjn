package utils;

import applications.search.configuration.SearchConfig;
import applications.search.models.QA;
import applications.search.models.Review;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.Reader;

/**
 * Parse Json into objects.
 *
 * @author Palak Jain
 */
public class JsonManager {

    /**
     * Parse Json to review object.
     * @param json One document written in Json format.
     * @return Review object
     */
    public static Review getReviewObject(String json) {
        Review review = null;

        try {
            Gson gson = new Gson();
            review = gson.fromJson(json, Review.class);
        }
        catch (JsonSyntaxException exception) {
            System.out.println("Fail to parse line: " + json);
        }

        return review;
    }

    /**
     * Parse Json to QA object
     * @param json One document written in Json format.
     * @return QA object
     */
    public static QA getQAObject(String json) {
        QA qa = null;

        try {
            Gson gson = new Gson();
            qa = gson.fromJson(json, QA.class);
        }
        catch (JsonSyntaxException exception) {
            System.out.println("Fail to parse line: " + json);
        }

        return qa;
    }

    /**
     * Parse JSON string into Configuration object
     * @param reader Reader object
     * @return Config object
     */
    public static SearchConfig fromJsonToConfig(Reader reader) {
        Gson gson = new Gson();

        SearchConfig config = null;

        try {
            config = gson.fromJson(reader, SearchConfig.class);
        }
        catch (JsonSyntaxException exception) {
            System.out.println("Unable to parse json");
        }

        return config;
    }
}
