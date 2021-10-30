package applications.search.models;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class QAListTest {
    static QAList qaList = new QAList();

    @BeforeAll
    public static void init() {
        QA qa1 = new QA("12345", "How are you", "I am fine");
        QA qa2 = new QA("21422", "And, you joined?", "yes!");
        qaList.add(0, qa1);
        qaList.add(1, qa2);
    }

    @Test
    public void toString_nullInput_returnEmptyString() {
        Assertions.assertEquals("", qaList.toString(null));
    }

    @Test
    public void toString_existAsin_returnValidResponse() {
        String expected = "<h3>Asin number: 12345. Questions & Answers: </h3><br />\n" +
                    "<p>1) Question: How are you, Answer: I am fine</p>\n";
        String actual = qaList.toString("12345");

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void toString_notExistAsin_returnEmptyResponse() {
        String expected = "";
        String actual = qaList.toString("76836");

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void toString_indexOutOfBound_returnNull() {
        String expected = null;
        String actual = qaList.toString(2);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void toString_indexInBound_returnValidString() {
        String expected = "ASIN Number: 12345. Question: How are you, Answer: I am fine";
        String actual = qaList.toString(0);

        Assertions.assertEquals(expected, actual);
    }
}
