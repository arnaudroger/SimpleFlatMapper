package org.simpleflatmapper.reflect.meta;

public class PropertyNameMatch {

    private final String property;
    private final String column;
    private final PropertyNameMatcher leftOverMatcher;
    public final int score;
    public final int skippedLetters;

    public PropertyNameMatch(String property, String column, PropertyNameMatcher leftOverMatcher, int score, int skippedLetters) {
        this.property = property;
        this.column = column;
        this.leftOverMatcher = leftOverMatcher;
        this.score = score;
        this.skippedLetters = skippedLetters;
    }

    public String getProperty() {
        return property;
    }

    public PropertyNameMatcher getLeftOverMatcher() {
        return leftOverMatcher;
    }
}
