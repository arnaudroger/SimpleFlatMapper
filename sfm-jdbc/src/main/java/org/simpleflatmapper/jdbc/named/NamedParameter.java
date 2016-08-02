package org.simpleflatmapper.jdbc.named;

public class NamedParameter extends Symbol {
    private final String name;

    public NamedParameter(String name, Position position) {
        super(position);
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
