package org.simpleflatmapper.jdbc.named;

public class NamedParameter extends Symbol {
    private final String name;

    public NamedParameter(String name, Position position) {
        super(position);
        this.name = stripQuotes(name);
    }

    private String stripQuotes(String name) {
        if (name.startsWith("\"")) {
            if (name.endsWith("\"")) {
                return name.substring(1, name.length() -1);
            }
        }
        if (name.startsWith("`")) {
            if (name.endsWith("`")) {
                return name.substring(1, name.length() -1);
            }
        }
        return name;
    }

    public String getName() {
        return name;
    }
}
