package org.simpleflatmapper.map.property;

import static org.simpleflatmapper.util.Asserts.requireNonNull;

public class RenameProperty {
    private final String name;

    public RenameProperty(String name) {
        this.name = requireNonNull("name", name);
    }

    public String getName() {
        return name;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RenameProperty that = (RenameProperty) o;

        return name.equals(that.name);

    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return "Rename{'" + name + "'}";
    }
}
