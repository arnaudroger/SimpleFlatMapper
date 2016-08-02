package org.simpleflatmapper.jdbc.named;

public class Word extends Symbol {
    private final String name;

    public Word(String name, Position position) {
        super(position);
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
