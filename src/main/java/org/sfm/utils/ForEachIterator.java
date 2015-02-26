package org.sfm.utils;


public interface ForEachIterator<T> {

    public boolean next(RowHandler<? super T> rowHandler) throws Exception;
    public void forEach(RowHandler<? super T> rowHandler) throws Exception;
}
