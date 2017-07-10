package org.simpleflatmapper.reflect.meta;

public class PropertyMatchingScore implements Comparable<PropertyMatchingScore> {
    
    
    public final static PropertyMatchingScore INITIAL = new PropertyMatchingScore(0, 0, 0);

    private final int selfNumberOfProperties;
    private final int nbMatch;
    private final int depth;
    

    private PropertyMatchingScore(int selfNumberOfProperties, int nbMatch, int depth) {
        this.nbMatch = nbMatch;
        this.selfNumberOfProperties = selfNumberOfProperties;
        this.depth = depth;
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
                this.depth + 1);
    }

    public PropertyMatchingScore matches(PropertyNameMatcher property) {
        return matches(property.toString());
    }
    public PropertyMatchingScore matches(String property) {
        return new PropertyMatchingScore(
                this.selfNumberOfProperties, 
                this.nbMatch + property.length(), 
                this.depth + 1);
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
                this.depth + i);
    }

    public PropertyMatchingScore newIndex(int i) {
        return new PropertyMatchingScore(
                this.selfNumberOfProperties,
                this.nbMatch,
                this.depth + i);
    }

    public PropertyMatchingScore self(int numberOfProperties, String propName) {
        return new PropertyMatchingScore(this.selfNumberOfProperties + numberOfProperties, this.nbMatch + (numberOfProperties == 0  ? propName.length() : 0 ), this.depth + 1);
    }

    public PropertyMatchingScore self(ClassMeta propertyMeta, String propName) {
        return self(propertyMeta.getNumberOfProperties(), propName);
    }

}
