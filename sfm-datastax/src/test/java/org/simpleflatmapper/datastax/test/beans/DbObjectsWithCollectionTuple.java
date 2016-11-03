package org.simpleflatmapper.datastax.test.beans;

import org.simpleflatmapper.tuple.Tuple2;

import java.util.Collection;

public class DbObjectsWithCollectionTuple {
    private long id;
    private Collection<Tuple2<String, Long>> l;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Collection<Tuple2<String, Long>> getL() {
        return l;
    }

    public void setL(Collection<Tuple2<String, Long>> l) {
        this.l = l;
    }

}
