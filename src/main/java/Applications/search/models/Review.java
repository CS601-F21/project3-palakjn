package applications.search.models;

import java.util.List;

/**
 * Holds information of one review.
 *
 * @author Palak Jain
 */
public class Review {

    private String reviewerID;
    private String asin;
    private String reviewerName;
    private List<Integer> helpful;
    private String reviewText;
    private float overall;
    private String summary;
    private long unixReviewTime;
    private String reviewTime;

    public Review(String asin, String reviewText) {
        this.asin = asin;
        this.reviewText = reviewText;
    }

    /**
     * Returns Asin of a product
     *
     * @return Asin
     */
    public String getAsin() {
        return asin;
    }

    /**
     * Returns reviewText
     *
     * @return ReviewText
     */
    public String getReviewText() {
        return reviewText;
    }

    /**
     * Returns a few details of Review as a string
     *
     * @return A formatted string
     */
    public String toString() {
        return String.format("ReviewerID: %s, ReviewerName: %s, ReviewerText: %s", reviewerID, reviewerName, reviewText);
    }
}
