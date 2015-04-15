package org.sfm.map.column;

public class RenameProperty implements ColumnProperty {
    private final String name;

    public RenameProperty(String name) {
        if (name == null) throw new NullPointerException();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Rename{'" + name + "'}";
    }
}
