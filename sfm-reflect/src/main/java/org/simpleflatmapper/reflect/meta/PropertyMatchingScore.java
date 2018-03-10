package org.simpleflatmapper.reflect.meta;

public class PropertyMatchingScore implements Comparable<PropertyMatchingScore> {

    private final int selfNumberOfProperties;
    private final int nbMatch;
    private final int verticalDepth;
    private final int horizontalDepth;
    private final boolean selfScoreFullName;

    private PropertyMatchingScore(int selfNumberOfProperties, int nbMatch, int verticalDepth, int horizontalDepth, boolean selfScoreFullName) {
        this.nbMatch = nbMatch;
        this.selfNumberOfProperties = selfNumberOfProperties;
        this.verticalDepth = verticalDepth;
        this.horizontalDepth = horizontalDepth;
        this.selfScoreFullName = selfScoreFullName;
    }


    @Override
    public int compareTo(PropertyMatchingScore o) {
        if (selfNumberOfProperties < o.selfNumberOfProperties) return -1;
        if (selfNumberOfProperties > o.selfNumberOfProperties) return 1;


        if (nbMatch < o.nbMatch) return 1;
        if (nbMatch > o.nbMatch) return -1;

        if (verticalDepth < o.verticalDepth) return -1;
        if (verticalDepth > o.verticalDepth) return 1;

        if (horizontalDepth < o.horizontalDepth) return -1;
        if (horizontalDepth > o.horizontalDepth) return 1;
        
        return 0;
    }

    public PropertyMatchingScore speculative() {
        return new PropertyMatchingScore(
                this.selfNumberOfProperties,
                this.nbMatch,
                verticalDepth, this.horizontalDepth + 1, selfScoreFullName);
    }

    public PropertyMatchingScore matches(PropertyNameMatcher property) {
        return matches(property.toString());
    }
    public PropertyMatchingScore matches(String property) {
        return new PropertyMatchingScore(
                this.selfNumberOfProperties, 
                this.nbMatch + property.length(),
                verticalDepth, this.horizontalDepth + 1, selfScoreFullName);
    }

    @Override
    public String toString() {
        return "{" +
                "selfNumberOfProperties=" + selfNumberOfProperties +
                ", nbMatch=" + nbMatch +
                ", horizontalDepth=" + horizontalDepth +
                '}';
    }

    public PropertyMatchingScore arrayIndex(int i) {
        return new PropertyMatchingScore(
                this.selfNumberOfProperties,
                this.nbMatch,
                verticalDepth, this.horizontalDepth  + i, selfScoreFullName);
    }

    public PropertyMatchingScore speculativeArrayIndex(int i) {
        return new PropertyMatchingScore(
                this.selfNumberOfProperties,
                this.nbMatch,
                verticalDepth + i, this.horizontalDepth, selfScoreFullName);
    }
    public PropertyMatchingScore tupleIndex(int i) {
        return new PropertyMatchingScore(
                this.selfNumberOfProperties,
                this.nbMatch,
                verticalDepth, this.horizontalDepth + i, selfScoreFullName);
    }

    public PropertyMatchingScore self(int numberOfProperties, String propName) {
        return new PropertyMatchingScore(this.selfNumberOfProperties + numberOfProperties, this.nbMatch + selfNbMatch(numberOfProperties, propName), verticalDepth, this.horizontalDepth + 1, selfScoreFullName);
    }

    private int selfNbMatch(int numberOfProperties, String propName) {
        return selfScoreFullName && numberOfProperties == 0  ? propName.length() : 0;
    }

    public PropertyMatchingScore self(ClassMeta propertyMeta, String propName) {
        return self(propertyMeta.getNumberOfProperties(), propName);
    }

    public static PropertyMatchingScore newInstance(boolean selfScoreFullName) {
        return new PropertyMatchingScore(0, 0, 0, 0, selfScoreFullName);
    }

}
