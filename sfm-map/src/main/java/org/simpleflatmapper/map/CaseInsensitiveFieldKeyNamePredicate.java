package org.simpleflatmapper.map;

import org.simpleflatmapper.util.Named;
import org.simpleflatmapper.util.OrManyPredicate;
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

    @Override
    public String toString() {
        return "CaseInsensitiveFieldKeyNamePredicate{" +
                "name='" + name + '\'' +
                '}';
    }

    public static CaseInsensitiveFieldKeyNamePredicate of(String name) {
        return new CaseInsensitiveFieldKeyNamePredicate(name);
    }

    public static Predicate<FieldKey<?>> any(String... name) {
        CaseInsensitiveFieldKeyNamePredicate[] predicates = new CaseInsensitiveFieldKeyNamePredicate[name.length];

        for (int i = 0; i < name.length; i++) {
            String n = name[i];
            predicates[i] = CaseInsensitiveFieldKeyNamePredicate.of(n);
        }

        return new OrManyPredicate<FieldKey<?>>(predicates);
    }
}
