package org.simpleflatmapper.reflect.meta;

public class PropertyMatchingScore implements Comparable<PropertyMatchingScore> {
    public final static PropertyMatchingScore INITIAL = new PropertyMatchingScore();

    private final long score;

    private PropertyMatchingScore() {
        this(Long.MAX_VALUE);
    }

    private PropertyMatchingScore(long score) {
        this.score = score;
    }

    public PropertyMatchingScore decrease(int i) {
        return new PropertyMatchingScore(score - i);
    }

    public PropertyMatchingScore shift() {
        return new PropertyMatchingScore(score / 2);
    }

    @Override
    public int compareTo(PropertyMatchingScore o) {
        long l = o.score - score;
        if (l > 0) return 1;
        if (l < 0) return -1;
        return 0;
    }
}
