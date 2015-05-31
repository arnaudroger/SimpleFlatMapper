package org.sfm.utils;

import java.util.Spliterator;
import java.util.function.Consumer;

public class EnumarableSpliterator<T> implements Spliterator<T> {

    private final Enumarable<T> enumarable;

    public EnumarableSpliterator(Enumarable<T> enumarable) {
        this.enumarable = enumarable;
    }

    @Override
    public boolean tryAdvance(Consumer<? super T> action) {
        if (enumarable.next()) {
            action.accept(enumarable.currentValue());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void forEachRemaining(Consumer<? super T> action) {
        while(enumarable.next()) {
            action.accept(enumarable.currentValue());
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
