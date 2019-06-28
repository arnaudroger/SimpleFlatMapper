package org.simpleflatmapper.map;

import org.simpleflatmapper.util.Named;
import org.simpleflatmapper.util.OrManyPredicate;
import org.simpleflatmapper.util.Predicate;

public final class CaseInsensitiveEndsWithPredicate implements Predicate<FieldKey<?>>, Named {
    private final String end;

    private CaseInsensitiveEndsWithPredicate(String name) {
        this.end = name;
    }

    @Override
    public boolean test(FieldKey<?> fieldKey) {
        String name = fieldKey.getName();

        if (name.length() < this.end.length()) return false;

        //    public boolean regionMatches(boolean ignoreCase, int toffset,
        //            String other, int ooffset, int len) {
        return name.regionMatches(true, name.length() - end.length(), end, 0, end.length() );
    }

    @Override
    public String getName() {
        return end;
    }

    @Override
    public String toString() {
        return "CaseInsensitiveEndsWithPredicate{" +
                "name='" + end + '\'' +
                '}';
    }

    public static CaseInsensitiveEndsWithPredicate of(String name) {
        return new CaseInsensitiveEndsWithPredicate(name);
    }

    public static Predicate<FieldKey<?>> any(String... name) {
        CaseInsensitiveEndsWithPredicate[] predicates = new CaseInsensitiveEndsWithPredicate[name.length];

        for (int i = 0; i < name.length; i++) {
            String n = name[i];
            predicates[i] = CaseInsensitiveEndsWithPredicate.of(n);
        }

        return new OrManyPredicate<FieldKey<?>>(predicates);
    }
}
