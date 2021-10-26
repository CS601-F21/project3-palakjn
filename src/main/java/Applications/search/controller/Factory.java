package applications.search.controller;

import applications.search.configuration.Constants;
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
    public static InvertedIndex getIndex(Constants.Type type) {
        InvertedIndex index = null;

        if(type == Constants.Type.REVIEW) {
            index = IndexCreator.getReviewIndex();
        }
        else if(type == Constants.Type.QA) {
            index = IndexCreator.getQaIndex();
        }

        return  index;
    }
}
