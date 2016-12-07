package org.simpleflatmapper.map;

import org.simpleflatmapper.util.Named;
import org.simpleflatmapper.util.Predicate;

public final class CaseInsensitiveFieldKeyNamePredicate implements Predicate<FieldKey<?>>, Named {
    private final String name;

    private CaseInsensitiveFieldKeyNamePredicate(String name) {
        this.name = name;
    }

    @Override
    public boolean test(FieldKey<?> fieldKey) {
        return fieldKey.getName().equalsIgnoreCase(name);
    }

    @Override
    public String getName() {
        return name;
    }

    public static CaseInsensitiveFieldKeyNamePredicate of(String name) {
        return new CaseInsensitiveFieldKeyNamePredicate(name);
    }
}
