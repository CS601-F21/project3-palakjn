package applications.search.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Maintains a data structure which maps terms to the documents where those terms appear.
 *
 * @author Palak Jain
 */
public class InvertedIndex {
    private HashMap<String, List<Integer>> wordMap;

    public InvertedIndex() {
        wordMap = new HashMap<>();
    }

    /**
     * Creates new entry by keeping term as key and value as its document reference or update existing one by adding new document where the term exist.
     * @param key one word term
     * @param documentIndex Holding an index of the actual document where the term exists.
     */
    public void upsert(String key, int documentIndex) {
        List<Integer> documentRefs = wordMap.getOrDefault(key, new ArrayList<>());
        if(!documentRefs.contains(documentIndex)) {
            //Not inserting if already captures that the document contains the term.
            documentRefs.add(documentIndex);
            wordMap.put(key, documentRefs);
        }
    }

    /**
     * Gets all the documents where the given term exists.
     * @param term One word term
     * @param isPartial
     * @return
     */
    public List<Integer> get(String term, boolean isPartial) {
        List<Integer> documents = new ArrayList<>();

        if(!isPartial) {
            documents = wordMap.getOrDefault(term, null);
        }
        else {
            for (Map.Entry<String, List<Integer>> entry : wordMap.entrySet()) {
                if(entry.getKey().contains(term)) {
                    documents.addAll(entry.getValue());
                }
            }
        }

        return documents;
    }
}
