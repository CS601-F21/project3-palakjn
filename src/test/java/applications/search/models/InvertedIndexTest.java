package applications.search.models;

import applications.search.controller.DataProcessor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.*;

public class InvertedIndexTest {
    static InvertedIndex invertedIndex = new InvertedIndex();

    @BeforeAll
    public static void init() {
        HashMap<String, HashSet<Integer>> wordMap = new HashMap<>();
        wordMap.put("the", new HashSet<>(Arrays.asList(0, 3, 4)));
        wordMap.put("term", new HashSet<>(Arrays.asList(0, 1,3)));
        wordMap.put("test", new HashSet<>(Arrays.asList(3,4,6)));
        wordMap.put("case", new HashSet<>(Arrays.asList(0,1,2,4)));
        wordMap.put("hello", new HashSet<>(Arrays.asList(1,2,3,4)));

        try {
            Field wordMapField = InvertedIndex.class.getDeclaredField("wordMap");
            wordMapField.setAccessible(true);
            wordMapField.set(invertedIndex, wordMap);
        } catch (NoSuchFieldException | IllegalAccessException exception) {
            System.err.println(exception.getMessage());
        }
    }

    @Test
    public void get_containTerm_returnValidDocuments() {
        List<Integer> expected = new ArrayList<>(Arrays.asList(0,1,2,4));
        List<Integer> actual = invertedIndex.get("case");

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void get_invalidTerm_returnNoDocuments() {
        List<Integer> expected = new ArrayList<>();
        List<Integer> actual = invertedIndex.get("c@se");

        Assertions.assertEquals(expected, actual);
    }
}
