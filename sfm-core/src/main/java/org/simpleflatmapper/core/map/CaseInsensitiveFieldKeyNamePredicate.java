package org.simpleflatmapper.core.map;

import org.simpleflatmapper.core.utils.Named;
import org.simpleflatmapper.core.utils.Predicate;

public class CaseInsensitiveFieldKeyNamePredicate implements Predicate<FieldKey<?>>, Named {
    private final String name;

    public CaseInsensitiveFieldKeyNamePredicate(String name) {
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
}
