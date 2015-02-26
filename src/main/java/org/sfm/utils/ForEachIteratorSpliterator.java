package org.sfm.utils;

import java.util.Spliterator;
import java.util.function.Consumer;

public class ForEachIteratorSpliterator<T> implements Spliterator<T> {
    private final ForEachIterator<T> iterator;

    public ForEachIteratorSpliterator(ForEachIterator<T> iterator) {
        this.iterator = iterator;
    }

    @Override
    public boolean tryAdvance(Consumer<? super T> action) {
        try {

            return iterator.next(new RowHandler<T>() {
                @Override
                public void handle(T t) throws Exception {
                    action.accept(t);
                }
            });
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void forEachRemaining(Consumer<? super T> action) {
        try {
            iterator.forEach(new RowHandler<T>() {
                @Override
                public void handle(T t) throws Exception {
                    action.accept(t);
                }
            });
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
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
        return Spliterator.ORDERED | Spliterator.NONNULL;
    }
}
