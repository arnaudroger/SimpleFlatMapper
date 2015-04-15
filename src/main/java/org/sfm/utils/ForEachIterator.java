package org.sfm.utils;

public interface ForEachIterator<T> {
    boolean next(RowHandler<? super T> rowHandler) throws Exception;
    void forEach(RowHandler<? super T> rowHandler) throws Exception;
}
