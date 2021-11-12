package applications.search.controller;

import applications.search.configuration.SearchConstants;
import applications.search.models.InvertedIndex;

/**
 * @author Palak Jain
 */
public class Factory {

    /**
     * Gives Inverted Index instance based on the type of instance.
     * @param type Either REVIEW or QA
     * @return Instance of Inverted Index
     */
    public static InvertedIndex getIndex(SearchConstants.Type type) {
        InvertedIndex index = null;

        if(type == SearchConstants.Type.REVIEW) {
            index = IndexCreator.getReviewIndex();
        }
        else if(type == SearchConstants.Type.QA) {
            index = IndexCreator.getQaIndex();
        }

        return  index;
    }
}
