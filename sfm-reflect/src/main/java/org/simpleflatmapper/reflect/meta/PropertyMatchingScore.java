package org.simpleflatmapper.reflect.meta;

public class PropertyMatchingScore implements Comparable<PropertyMatchingScore> {

    private final int selfNumberOfProperties;
    private final int nbMatch;
    private final int depth;
    private final boolean selfScoreFullName;

    private PropertyMatchingScore(int selfNumberOfProperties, int nbMatch, int depth, boolean selfScoreFullName) {
        this.nbMatch = nbMatch;
        this.selfNumberOfProperties = selfNumberOfProperties;
        this.depth = depth;
        this.selfScoreFullName = selfScoreFullName;
    }


    @Override
    public int compareTo(PropertyMatchingScore o) {
        if (selfNumberOfProperties < o.selfNumberOfProperties) return -1;
        if (selfNumberOfProperties > o.selfNumberOfProperties) return 1;


        if (nbMatch < o.nbMatch) return 1;
        if (nbMatch > o.nbMatch) return -1;

        if (depth < o.depth) return -1;
        if (depth > o.depth) return 1;
        
        return 0;
    }

    public PropertyMatchingScore speculative() {
        return new PropertyMatchingScore(
                this.selfNumberOfProperties,
                this.nbMatch,
                this.depth + 1, selfScoreFullName);
    }

    public PropertyMatchingScore matches(PropertyNameMatcher property) {
        return matches(property.toString());
    }
    public PropertyMatchingScore matches(String property) {
        return new PropertyMatchingScore(
                this.selfNumberOfProperties, 
                this.nbMatch + property.length(), 
                this.depth + 1, selfScoreFullName);
    }

    @Override
    public String toString() {
        return "{" +
                "selfNumberOfProperties=" + selfNumberOfProperties +
                ", nbMatch=" + nbMatch +
                ", depth=" + depth +
                '}';
    }

    public PropertyMatchingScore index(int i) {
        return new PropertyMatchingScore(
                this.selfNumberOfProperties,
                this.nbMatch,
                this.depth + i, selfScoreFullName);
    }

    public PropertyMatchingScore newIndex(int i) {
        return new PropertyMatchingScore(
                this.selfNumberOfProperties,
                this.nbMatch,
                this.depth + i, selfScoreFullName);
    }

    public PropertyMatchingScore self(int numberOfProperties, String propName) {
        return new PropertyMatchingScore(this.selfNumberOfProperties + numberOfProperties, this.nbMatch + selfNbMatch(numberOfProperties, propName), this.depth + 1, selfScoreFullName);
    }

    private int selfNbMatch(int numberOfProperties, String propName) {
        return selfScoreFullName && numberOfProperties == 0  ? propName.length() : 0;
    }

    public PropertyMatchingScore self(ClassMeta propertyMeta, String propName) {
        return self(propertyMeta.getNumberOfProperties(), propName);
    }

    public static PropertyMatchingScore newInstance(boolean selfScoreFullName) {
        return new PropertyMatchingScore(0, 0, 0, selfScoreFullName);
    }
}
