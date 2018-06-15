package org.simpleflatmapper.map.property;

import org.simpleflatmapper.map.FieldKey;
import org.simpleflatmapper.reflect.meta.DisallowSelfReference;
import org.simpleflatmapper.util.Function;

import static org.simpleflatmapper.util.Asserts.requireNonNull;

/**
 * Map an incoming/outgoing column name to the actual object property name.
 */
public class RenameProperty implements DisallowSelfReference {
    private final Function<String, String> renameFunction;

    /**
     * Create a rename property to resolve the column name to the property name.
     * @param renameFunction the function that transform the incoming/outgoing name to the actual object property name.
     */
    public RenameProperty(Function<String, String> renameFunction) {
        this.renameFunction = renameFunction;
    }

    /**
     * Create a rename property to resolve the column name to the property name.
     * @param name to the actual object property name.
     */
    public RenameProperty(String name) {
        this(new ConstantRename(name));
    }

    public <K extends FieldKey<K>> K apply(K key) {
        return key.alias(renameFunction.apply(key.getName()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RenameProperty that = (RenameProperty) o;

        return renameFunction.equals(that.renameFunction);

    }

    @Override
    public int hashCode() {
        return renameFunction.hashCode();
    }

    @Override
    public String toString() {
        return "Rename{" + renameFunction + "}";
    }

    private static class ConstantRename implements Function<String, String> {
        private final String name;

        private ConstantRename(String name) {
            this.name = requireNonNull("name", name);
        }

        @Override
        public String apply(String s) {
            return name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ConstantRename that = (ConstantRename) o;

            return name.equals(that.name);

        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }

        @Override
        public String toString() {
            return '\'' + name + '\'';
        }
    }
}
