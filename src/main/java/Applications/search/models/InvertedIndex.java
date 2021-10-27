package applications.search.models;

import java.util.*;

/**
 * Maintains a data structure which maps terms to the documents where those terms appear.
 *
 * @author Palak Jain
 */
public class InvertedIndex {
    private HashMap<String, HashSet<Integer>> wordMap;

    public InvertedIndex() {
        wordMap = new HashMap<>();
    }

    /**
     * Creates new entry by keeping term as key and value as its document reference or update existing one by adding new document where the term exist.
     * @param key one word term
     * @param documentIndex Holding an index of the actual document where the term exists.
     */
    public void upsert(String key, int documentIndex) {
        HashSet<Integer> documentRefs = wordMap.getOrDefault(key, new HashSet<>());
        documentRefs.add(documentIndex);
        wordMap.put(key, documentRefs);
    }

    /**
     * Gets all the documents where the given term exists.
     * @param term One word term
     * @return
     */
    public List<Integer> get(String term) {
        List<Integer> documents = new ArrayList<>();

        for (Map.Entry<String, HashSet<Integer>> entry : wordMap.entrySet()) {
            if(entry.getKey().contains(term)) {
                documents.addAll(entry.getValue());
            }
        }

        return documents;
    }
}
