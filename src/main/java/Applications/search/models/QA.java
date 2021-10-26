package applications.search.models;

/**
 * Holds information of one Question and Answer.
 *
 * @author Palak Jain
 */
public class QA {

    private String questionType;
    private String asin;
    private String answerTime;
    private long unixTime;
    private String question;
    private String answerType;
    private String answer;

    public QA(String asin, String question, String answer) {
        this.asin = asin;
        this.question = question;
        this.answer = answer;
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
     * Returns Question
     *
     * @return Question
     */
    public String getQuestion() {
        return question;
    }

    /**
     * Returns Answer
     *
     * @return Answer
     */
    public String getAnswer() {
        return answer;
    }

    /**
     * Returns a few details of QA as a string
     *
     * @return A formatted string
     */
    public String toString() {
        return String.format("Question: %s, Answer: %s", question, answer);
    }
}
