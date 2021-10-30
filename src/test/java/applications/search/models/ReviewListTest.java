package applications.search.models;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ReviewListTest {
    static ReviewList reviewList = new ReviewList();

    @BeforeAll
    public static void init() {
        Review review1 = new Review("12345", "The dog and the cat");
        Review review2 = new Review("67891", "Hello, the sample review");
        reviewList.add(0, review1);
        reviewList.add(1, review2);
    }

    @Test
    public void toString_nullInput_returnEmptyString() {
        Assertions.assertEquals("", reviewList.toString(null));
    }

    @Test
    public void toString_existAsin_returnValidResponse() {
        String expected = "<h3>Asin number: 12345. Reviews: </h3><br />\n" +
                "<p>1) ReviewerID: null, ReviewerName: null, ReviewerText: The dog and the cat</p>\n";
        String actual = reviewList.toString("12345");

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void toString_notExistAsin_returnEmptyResponse() {
        String expected = "";
        String actual = reviewList.toString("45637");

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void toString_indexOutOfBound_returnNull() {
        String expected = null;
        String actual = reviewList.toString(2);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void toString_indexInBound_returnValidString() {
        String expected = "ASIN Number: 12345. ReviewerID: null, ReviewerName: null, ReviewerText: The dog and the cat";
        String actual = reviewList.toString(0);

        Assertions.assertEquals(expected, actual);
    }
}
