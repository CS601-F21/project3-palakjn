package applications.search.models;

import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Maintains a data structure which maps terms to the documents where those terms appear.
 *
 * @author Palak Jain
 */
public class InvertedIndex {
    private HashMap<String, HashSet<Integer>> wordMap;
    private ReentrantReadWriteLock lock;

    public InvertedIndex() {
        wordMap = new HashMap<>();
        this.lock = new ReentrantReadWriteLock();
    }

    /**
     * Creates new entry by keeping term as key and value as its document reference or update existing one by adding new document where the term exist.
     * @param key one word term
     * @param documentIndex Holding an index of the actual document where the term exists.
     */
    public void upsert(String key, int documentIndex) {
        this.lock.writeLock().lock();

        try {
            HashSet<Integer> documentRefs = wordMap.getOrDefault(key, new HashSet<>());
            documentRefs.add(documentIndex);
            wordMap.put(key, documentRefs);
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    /**
     * Gets all the documents where the given term exists.
     * @param term One word term
     * @return
     */
    public List<Integer> get(String term) {
        this.lock.readLock().lock();

        try {
            List<Integer> documents = new ArrayList<>();

            for (Map.Entry<String, HashSet<Integer>> entry : wordMap.entrySet()) {
                if(entry.getKey().contains(term)) {
                    documents.addAll(entry.getValue());
                }
            }

            return documents;
        } finally {
            this.lock.readLock().unlock();
        }
    }
}
