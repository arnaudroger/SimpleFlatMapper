package org.simpleflatmapper.util;

import java.util.Spliterator;
import java.util.function.Consumer;

public class EnumarableSpliterator<T> implements Spliterator<T> {

    private final Enumarable<T> enumarable;

    public EnumarableSpliterator(Enumarable<T> enumarable) {
        this.enumarable = enumarable;
    }

    @Override
    public boolean tryAdvance(Consumer<? super T> action) {
        Enumarable<T> lEnumarable = this.enumarable;
        if (lEnumarable.next()) {
            action.accept(lEnumarable.currentValue());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void forEachRemaining(Consumer<? super T> action) {
        Enumarable<T> lEnumarable = this.enumarable;
        while(lEnumarable.next()) {
            action.accept(lEnumarable.currentValue());
        }
    }

    @Override
    public Spliterator<T> trySplit() {
        return null;
    }

    @Override
    public long estimateSize() {
        return Long.MAX_VALUE;
    }

    @Override
    public int characteristics() {
        return ORDERED;
    }
}
