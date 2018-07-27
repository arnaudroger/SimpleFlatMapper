package org.simpleflatmapper.util;

import java.util.Spliterator;
import java.util.function.Consumer;

public class EnumerableSpliterator<T> implements Spliterator<T> {

    private final Enumerable<T> enumerable;

    public EnumerableSpliterator(Enumerable<T> enumerable) {
        this.enumerable = enumerable;
    }

    @Override
    public boolean tryAdvance(Consumer<? super T> action) {
        Enumerable<T> lEnumerable = this.enumerable;
        if (lEnumerable.next()) {
            action.accept(lEnumerable.currentValue());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void forEachRemaining(Consumer<? super T> action) {
        Enumerable<T> lEnumerable = this.enumerable;
        while(lEnumerable.next()) {
            action.accept(lEnumerable.currentValue());
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
