package applications.search.models;

import java.util.ArrayList;
import java.util.List;

/**
 * A collection of reviews from the review dataset. The user will have a control on which index to add a new object.
 * The user can get a customized string representation of object fields depending on an input value.
 *
 * @author Palak Jain
 */
public class ReviewList {

    private List<Review> allReviews;

    public ReviewList() {
        allReviews = new ArrayList<>();
    }

    /**
     * Adding review object to a collection at particular index.
     *
     * @param index Index of a list
     * @param review Distinct review object
     */
    public void add(int index, Review review) {
        if(index != -1) {
            allReviews.add(index, review);
        }
    }

    /**
     * Gets customized string representation of object fields whose ASIN number is the given input.
     *
     * @param asin ASIN number of a product
     * @return A formatted string
     */
    public String toString(String asin) {
        StringBuilder stringBuilder = new StringBuilder();
        //Getting those reviews whose asin number is equal to arg "asin" value.
        List<Review> reviews = allReviews.stream().filter(o -> o.getAsin().equalsIgnoreCase(asin)).toList();
        int bulletNumbers = 1;

        if(reviews.size() > 0) {
            stringBuilder.append(String.format("<h3>Asin number: %s. Reviews: </h3><br />\n", asin));

            for (Review review : reviews) {
                stringBuilder.append(String.format("<p>%d) %s</p>\n", bulletNumbers, review.toString()));
                bulletNumbers++;
            }
        }

        return stringBuilder.toString();
    }

    /**
     * Gets customized string representation of field values of review object at a provided index.
     *
     * @param index Index of ArrayList
     * @return A formatted string
     */
    public String toString(int index) {
        String output = null;

        if(index >= 0 && index < allReviews.size()) {
            Review review = allReviews.get(index);
            output = String.format("ASIN Number: %s. %s", review.getAsin(), review.toString());
        }

        return output;
    }

    /**
     * Returns the number of elements in a list
     *
     * @return The number of elements in a list.
     */
    public int getCount() {
        return allReviews.size();
    }
}
