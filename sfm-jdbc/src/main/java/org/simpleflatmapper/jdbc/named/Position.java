package org.simpleflatmapper.jdbc.named;

public class Position {
    private final int start;
    private final int end;

    public Position(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }
}
