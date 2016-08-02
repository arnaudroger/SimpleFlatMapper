package org.simpleflatmapper.core.map.column;

import static org.simpleflatmapper.core.utils.Asserts.requireNonNull;

public class RenameProperty implements ColumnProperty {
    private final String name;

    public RenameProperty(String name) {
        this.name = requireNonNull("name", name);
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Rename{'" + name + "'}";
    }
}
