package org.simpleflatmapper.jdbc.named;

public class Symbol {
    private final Position position;

    public Symbol(Position position) {
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }
}
