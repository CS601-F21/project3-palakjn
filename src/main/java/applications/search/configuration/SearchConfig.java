package applications.search.configuration;

public class SearchConfig {
    private String reviewDatasetPath;
    private String qaDatasetPath;

    public SearchConfig(String reviewDatasetPath, String qaDatasetPath) {
        this.reviewDatasetPath = reviewDatasetPath;
        this.qaDatasetPath = qaDatasetPath;
    }

    /**
     * Get a file location of Home_And_Kitchen_5 review dataset
     * @return file location
     */
    public String getReviewDatasetPath() {
        return reviewDatasetPath;
    }

    /**
     * Get a file location of Apps_for_Android_5 review dataset
     * @return file location
     */
    public String getQaDatasetPath() {
        return qaDatasetPath;
    }


}
