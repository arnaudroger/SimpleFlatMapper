package org.simpleflatmapper.util;

public interface CharPredicate {
    boolean apply(char c);

    //IFJAVA8_START
    default CharPredicate or(CharPredicate cp) {
        return c -> this.apply(c) || cp.apply(c);
    }
    default CharPredicate and(CharPredicate cp) {
        return c -> this.apply(c) && cp.apply(c);
    }
    default CharPredicate negate() {
        return c -> !this.apply(c);
    }
    //IFJAVA8_END
}
