package org.simpleflatmapper.reflect.meta;

public class PropertyMatchingScore implements Comparable<PropertyMatchingScore> {

    private final int selfNumberOfProperties;
    private final int nbMatch;
    private final int nbPartialMatch;
    private final int verticalDepth;
    private final int horizontalDepth;
    private final boolean selfScoreFullName;
    private final boolean notMatched;
    private final boolean self;

    public PropertyMatchingScore(int selfNumberOfProperties, int nbMatch, int nbPartialMatch, int verticalDepth, int horizontalDepth, boolean selfScoreFullName, boolean notMatched, boolean self) {
        this.nbMatch = nbMatch;
        this.selfNumberOfProperties = selfNumberOfProperties;
        this.nbPartialMatch = nbPartialMatch;
        this.verticalDepth = verticalDepth;
        this.horizontalDepth = horizontalDepth;
        this.selfScoreFullName = selfScoreFullName;
        this.notMatched = notMatched;
        this.self = self;
    }


    @Override
    public int compareTo(PropertyMatchingScore o) {
        if (notMatched && !o.notMatched) return 1;
        if (!notMatched && o.notMatched) return -1;

        if (selfNumberOfProperties != o.selfNumberOfProperties
            && nbPartialMatch != o.nbPartialMatch) {
            // take most matches
            if (nbMatch < o.nbMatch) return 1;
            if (nbMatch > o.nbMatch) return -1;
        }
        if (nbPartialMatch < o.nbPartialMatch) return -1;
        if (nbPartialMatch > o.nbPartialMatch) return 1;

        if (nbMatch < o.nbMatch) return 1;
        if (nbMatch > o.nbMatch) return -1;

        if (verticalDepth < o.verticalDepth) return -1;
        if (verticalDepth > o.verticalDepth) return 1;

//        if (self && !o.self) return 1;
//        if (!self && o.self) return -1;

        if (horizontalDepth < o.horizontalDepth) return -1;
        if (horizontalDepth > o.horizontalDepth) return 1;

        if (selfNumberOfProperties < o.selfNumberOfProperties) return -1;
        if (selfNumberOfProperties > o.selfNumberOfProperties) return 1;

        return 0;
    }

    public PropertyMatchingScore speculative() {
        return new PropertyMatchingScore(
                this.selfNumberOfProperties,
                this.nbMatch,
                nbPartialMatch, verticalDepth, this.horizontalDepth + 1, selfScoreFullName, notMatched, self);
    }

    public PropertyMatchingScore matches(PropertyNameMatch property) {
        return partialMatch(property.score, property.skippedLetters);
    }

    public PropertyMatchingScore matches(PropertyNameMatcher property) {
        return matches(property.asScore());
    }
    public PropertyMatchingScore matches(String property) {
        int score = DefaultPropertyNameMatcher.toScore(property);
        return matches(score);
    }

    public PropertyMatchingScore matches(int score) {
        return new PropertyMatchingScore(
                this.selfNumberOfProperties,
                this.nbMatch + score,
                nbPartialMatch, verticalDepth,
                this.horizontalDepth + 1,
                selfScoreFullName, notMatched, self);
    }
    public PropertyMatchingScore partialMatch(int score, int pScore) {
        return new PropertyMatchingScore(
                this.selfNumberOfProperties,
                this.nbMatch + score,
                nbPartialMatch + pScore,
                verticalDepth, this.horizontalDepth + 1,
                selfScoreFullName, notMatched, self);
    }

    @Override
    public String toString() {
        return "PropertyMatchingScore{" +
                "selfNumberOfProperties=" + selfNumberOfProperties +
                ", nbMatch=" + nbMatch +
                ", nbPartialMatch=" + nbPartialMatch +
                ", verticalDepth=" + verticalDepth +
                ", horizontalDepth=" + horizontalDepth +
                ", selfScoreFullName=" + selfScoreFullName +
                ", notMatched=" + notMatched +
                ", self=" + self +
                '}';
    }

    public PropertyMatchingScore arrayIndex(IndexedColumn i) {
        return new PropertyMatchingScore(
                this.selfNumberOfProperties,
                this.nbMatch + (selfScoreFullName ? i.getScore() : 0),
                nbPartialMatch, verticalDepth,
                this.horizontalDepth  + i.getIndexValue(), selfScoreFullName, notMatched, self);
    }

    public PropertyMatchingScore speculativeArrayIndex(int i) {
        return new PropertyMatchingScore(
                this.selfNumberOfProperties,
                this.nbMatch,
                nbPartialMatch, verticalDepth + i, this.horizontalDepth + i, selfScoreFullName, notMatched, self);
    }
    public PropertyMatchingScore tupleIndex(int i) {
        return new PropertyMatchingScore(
                this.selfNumberOfProperties,
                this.nbMatch,
                nbPartialMatch, verticalDepth, this.horizontalDepth + i, selfScoreFullName, notMatched, self);
    }

    public PropertyMatchingScore self(int numberOfProperties, String propName) {
        return new PropertyMatchingScore(this.selfNumberOfProperties + numberOfProperties, this.nbMatch + selfNbMatch(numberOfProperties, propName), nbPartialMatch, verticalDepth, this.horizontalDepth + 1, selfScoreFullName, notMatched, true);
    }

    private int selfNbMatch(int numberOfProperties, String propName) {
        return selfScoreFullName && numberOfProperties == 0  ? effectiveLength(propName) : 0;
    }

    private int effectiveLength(String propName) {
        int s = 0;
        for(int i = 0; i < propName.length(); i++) {
            if (!DefaultPropertyNameMatcher.isSeparatorChar(propName.charAt(i))) s++;
        }
        return s;
    }

    public PropertyMatchingScore self(ClassMeta propertyMeta, String propName) {
        return self(propertyMeta.getNumberOfProperties(), propName);
    }
    public PropertyMatchingScore notMatch() {
        return new PropertyMatchingScore(this.selfNumberOfProperties, this.nbMatch, nbPartialMatch, verticalDepth, this.horizontalDepth + 1, selfScoreFullName, true, self);
    }

    public static PropertyMatchingScore newInstance(boolean selfScoreFullName) {
        return new PropertyMatchingScore(0, 0, 0, 0, 0, selfScoreFullName, false, false);
    }

}
