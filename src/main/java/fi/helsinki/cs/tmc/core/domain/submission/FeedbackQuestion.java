package fi.helsinki.cs.tmc.core.domain.submission;

public class FeedbackQuestion {

    private int id;
    private String question;
    private String kind;
    private int max = 0;
    private int min = 0;

    public int getId() {
        return id;
    }

    public String getKind() {
        return kind;
    }

    public String getQuestion() {
        return question;
    }

    public void setId(int id) {
        this.id = id;
    }

    /**
     * Sets kind and range limits if kind is integer range.
     */
    public void setKind(String kind) {
        this.kind = kind;
        if (this.isIntRange()) {
            setRangeLimits();
        }
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public boolean isIntRange() {
        return this.kind.matches("intrange\\[-?[0-9]+\\.\\.-?[0-9]+\\]");
    }

    public boolean isText() {
        return this.kind.equals("text");
    }

    public int getIntRangeMin() {
        setRangeLimits(); //TODO: This is silly
        return this.min;
    }

    public int getIntRangeMax() {
        setRangeLimits(); //TODO: This is silly
        return this.max;
    }

    /**
     * Parse the value of kind to get the limits of the intrange, and set them.
     */
    private void setRangeLimits() {
        String[] bounds = parseKind();
        if (bounds.length != 2) {
            throw new IllegalStateException("Parsing kind failed, maybe not an intrange question?");
        }
        min = Integer.parseInt(bounds[0]);
        max = Integer.parseInt(bounds[1]);

        if (min > max) {
            throw new IllegalStateException(
                    "Intrange lower bound must be smaller than upper bound. Got: " + kind);
        }
    }

    private String[] parseKind() {
        String range = kind;
        range = range.substring("intrange[".length(), kind.length() - 1);
        return range.split("\\.\\.");
    }
}
