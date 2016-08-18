package org.simpleflatmapper.converter;

public class ConvertingScore {
    public static final int MAX_SCORE = 256;
    public static final ConvertingScore NO_MATCH = new ConvertingScore(-1, -1);

    private final int fromScore;
    private final int toScore;

    public ConvertingScore(int fromScore, int toScore) {
        this.fromScore = fromScore;
        this.toScore = toScore;
    }

    public int getScore() {
        if (toScore == -1) return -1;

        // score on source
        if (fromScore == -1) return -1;

        return toScore * MAX_SCORE  + fromScore;
    }

    public int getFromScore() {
        return fromScore;
    }

    public int getToScore() {
        return toScore;
    }
}
